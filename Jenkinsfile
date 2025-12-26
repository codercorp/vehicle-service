pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    environment {
        SERVICE_NAME    = "vehicle-service"
        IMAGE_NAME      = "erdigvijay/devops_repo:vehicle-service-${BUILD_NUMBER}"
        K8S_NAMESPACE   = "automotive"
        DEPLOYMENT_NAME = "vehicle-service"
    }

    stages {

        stage('Checkout Code') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/codercorp/vehicle-service.git'
            }
        }

        stage('Compile') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('Unit Tests + JaCoCo') {
            steps {
                sh 'mvn test'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube_Jenkins') {
                    sh '''
                        mvn sonar:sonar \
                        -Dsonar.projectKey=vehicle-service \
                        -Dsonar.projectName=vehicle-service
                    '''
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Package JAR') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh "docker build -t ${IMAGE_NAME} ."
            }
        }

        stage('Docker Login') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-creds',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )
                ]) {
                    sh '''
                        echo "$DOCKER_PASS" | docker login \
                        -u "$DOCKER_USER" --password-stdin
                    '''
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                sh "docker push ${IMAGE_NAME}"
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                sh """
                    kubectl apply -f vehicle-service.yaml
                    kubectl set image deployment/${DEPLOYMENT_NAME} \
                    vehicle-service=${IMAGE_NAME} \
                    -n ${K8S_NAMESPACE}
                """
            }
        }

        stage('Verify Rollout') {
            steps {
                sh """
                    kubectl rollout status deployment/${DEPLOYMENT_NAME} -n ${K8S_NAMESPACE}
                    kubectl get pods -n ${K8S_NAMESPACE} -l app=vehicle-service
                """
            }
        }
    }

    post {

        success {
            emailext(
                subject: "✅ SUCCESS: ${JOB_NAME} #${BUILD_NUMBER}",
                mimeType: 'text/html',
                body: """
                    <h2 style="color:green;">Build Successful 🎉</h2>
                    <p><b>Service:</b> vehicle-service</p>
                    <p><b>Build:</b> #${BUILD_NUMBER}</p>
                    <p><b>Docker Image:</b> ${IMAGE_NAME}</p>
                    <p>
                        <b>Jenkins:</b>
                        <a href="${BUILD_URL}">${BUILD_URL}</a>
                    </p>
                """,
                to: "erdigvijaypatil01@gmail.com"
            )
        }

        failure {
            emailext(
                subject: "❌ FAILURE: ${JOB_NAME} #${BUILD_NUMBER}",
                mimeType: 'text/html',
                body: """
                    <h2 style="color:red;">Build Failed ❌</h2>
                    <p><b>Service:</b> vehicle-service</p>
                    <p><b>Build:</b> #${BUILD_NUMBER}</p>
                    <p>
                        <b>Logs:</b>
                        <a href="${BUILD_URL}">${BUILD_URL}</a>
                    </p>
                """,
                to: "erdigvijaypatil01@gmail.com"
            )
        }

        always {
            sh "docker logout || true"
            sh "docker image prune -f || true"
        }
    }
}
