
pipeline {

    agent any

    tools {
        maven 'maven-3.9'
    }

    stages{
    
        stage("build jar") {
    
            steps {
                script {
                    echo "building the application..."
                    sh 'mvn package  -DskipTests'
                }
            }
    
        }
    
        stage("build image") {
    
            steps {
                script {
                    echo "building the docker image..."
                    withCredentials([usernamePassword(credentialsId: 'docker-hub', usernameVariable: 'USER', passwordVariable: 'PASS')]) {
                        sh 'docker build -t diaa96/shopme:backend-1.0 ./ShopmeWebParent/ShopmeBackend/'
                        sh 'docker build -t diaa96/shopme:front-1.0 ./ShopmeWebParent/ShopmeFrontend/'
                    }
                }
            }
    
        }
    
        stage("deploy") {
    
            steps {
                script {
    
                    echo "Deploying to ${ENV}"
                }
            }
        }
    }
}