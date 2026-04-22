pipeline {
    agent any

    tools {
        maven 'Maven3'
        jdk 'C:\\Program Files\\Java\\jdk-21'
    }

    environment {
        PATH = "C:\\Program Files\\Docker\\Docker\\resources\\bin;${env.PATH}"
        JAVA_HOME = 'C:\\Program Files\\Java\\jdk-21'
        SONARQUBE_SERVER = 'SonarQubeServer'

        SONAR_TOKEN = credentials('SONAR_TOKEN')

        DOCKERHUB_CREDENTIALS_ID = 'docker-hub-cred'
        DOCKERHUB_REPO = 'osamaaa1/shopping-cart'
        DOCKER_IMAGE_TAG = 'v1'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/Osama-Aamer/SoftwareProject2-W1.git'
            }
        }

        stage('Build') {
            steps {
                bat 'mvn clean install -DskipTests'
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

        stage('SonarQube Analysis') {
            steps {
                // withSonarQubeEnv uses the server name configured in Jenkins System settings
                withSonarQubeEnv("${SONARQUBE_SERVER}") {
                    bat """
                        \${tool 'SonarScanner'}\\bin\\sonar-scanner ^
                        -Dsonar.projectKey=org.example:week1-swp2 ^
                        -Dsonar.sources=src/main/java ^
                        -Dsonar.projectName="Shopping Cart GUI" ^
                        -Dsonar.host.url=http://localhost:9000 ^
                        -Dsonar.login=${SONAR_TOKEN} ^
                        -Dsonar.java.binaries=target/classes
                    """
                }
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
            // Publishes the results of your unit tests in the Jenkins UI
            junit 'target/surefire-reports/*.xml'
        }
    }
}