def buildAndPushDockerImage() {
    def registry = "https://index.docker.io/v1/"
    def credentials = credentials('last-docker-token')
    def imageName = "${registry}/maven:3.8.3-openjdk-17:${env.BUILD_NUMBER}"

    sh "docker build -t ${imageName} ."
    withDockerRegistry(credentialsId: 'last-docker-token', url: registry) {
        sh "docker push ${imageName}"
    }
}
