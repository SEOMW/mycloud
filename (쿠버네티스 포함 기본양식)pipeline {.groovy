pipeline {
    agent any
    environment {
        DOCKER_REGISTRY = "https://index.docker.io/v1/"
        DOCKER_REGISTRY_CREDENTIALS = credentials('last-docker-token')
        KUBE_CONFIG = credentials('your-kube-config-id')
        GC_PSW = credentials('private-project') // 생성해야 합니다.
        GIT_REPO = 'private-project'
        GIT_USERNAME = 'SEOMW'
        TAG_VERSION = 'v1.0.0'
    }
    triggers {
        githubPush()
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Build, Test, Package') {
            steps {
                sh 'mvn clean install'
            }
        }
        stage('Build Docker Image') {
            steps {
                script {
                    docker.withRegistry("${DOCKER_REGISTRY}", "${DOCKER_REGISTRY_CREDENTIALS}") {
                        def imageName = "${DOCKER_REGISTRY}/maven:3.8.3-openjdk-17:${env.BUILD_NUMBER}"
                        docker.build(imageName, '.')
                        docker.push(imageName)
                    }
                }
            }
        }
        stage('Deploy to Kubernetes') {
            steps {
                script {
                    kubeconfig = credentials("${KUBE_CONFIG}")
                    sh "kubectl --kubeconfig=${kubeconfig} apply -f your-kubernetes-deployment.yaml"
                }
            }
        }
        stage('github create release') {
            steps {
                script {
                    def response = sh(script: """
                        curl -sSL \
                          -X POST \
                          -H 'Accept: application/vnd.github.v3+json' \
                          -H 'Authorization: Bearer ${GC_PSW}' \
                          -H 'X-GitHub-Api-Version: 2022-11-28' \
                          https://api.github.com/repos/${GIT_USERNAME}/${GIT_REPO}/releases \
                          -d '{
                                  "tag_name":"${TAG_VERSION}",
                                  "target_commitish":"main",
                                  "name":"Release ${TAG_VERSION}",
                                  "body":"Description of the release",
                                  "draft":false,
                                  "prerelease":false,
                                  "generate_release_notes":false
                              }'
                    """, returnStdout: true)

                    sh "echo '$response'"

                    def json = readJSON text: "$response"
                    def id = json.id

                    sh "mv target/demo-0.0.1-SNAPSHOT.war ${GIT_REPO}-${TAG_VERSION}.war"

                    sh """
                        curl -L \
                              -X POST \
                              -H "Accept: application/vnd.github.v3+json" \
                              -H "Authorization: Bearer ${GC_PSW}" \
                              -H "X-GitHub-Api-Version: 2022-11-28" \
                              -H "Content-Type: application/octet-stream" \
                              "https://uploads.github.com/repos/${GIT_USERNAME}/${GIT_REPO}/releases/${id}/assets?name=${GIT_REPO}-${TAG_VERSION}.war" \
                              --data-binary "@${GIT_REPO}-${TAG_VERSION}.war"
                    """
                }
            }
        }
    }
}