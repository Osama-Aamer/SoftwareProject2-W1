pipeline {
    agent any

    tools {
        maven 'Maven3'
    }

    environment {
        DOCKERHUB_CREDENTIALS_ID = 'docker-hub-cred'
        DOCKERHUB_REPO = 'osamaaa1/shopping-cart'
        DOCKER_IMAGE_TAG = 'v1'
        GITHUB_REPO = 'https://github.com/Osama-Aamer/SoftwareProject2-W1.git'
    }

    stages {
        stage('Build') {
            steps {
                bat 'mvn clean package'
            }
        }

        stage('Test') {
            steps {
                bat 'mvn test'
            }
        }

        stage('Generate JaCoCo Coverage Report') {
            steps {
                bat 'mvn jacoco:report'
            }
        }

        stage('Publish Coverage Report') {
            steps {
                jacoco()
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    docker.build("${DOCKERHUB_REPO}:${DOCKER_IMAGE_TAG}")
                }
            }
        }

        stage('Push Docker Image to Docker Hub') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', DOCKERHUB_CREDENTIALS_ID) {
                        docker.image("${DOCKERHUB_REPO}:${DOCKER_IMAGE_TAG}").push()
                        docker.image("${DOCKERHUB_REPO}:${DOCKER_IMAGE_TAG}").push('latest')
                    }
                }
            }
        }
    }

    post {
        always {
            junit 'target/surefire-reports/*.xml'
        }
        success {
            echo 'Pipeline succeeded! Docker image pushed to Docker Hub.'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}
