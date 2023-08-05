pipeline {

    agent any

    // environment {
    //     NEW_VERSION = '1.0'
    //     SERVER_CERDENTIALS = credentials('test-in-file')
    // }

    // tools {
    //     maven 'maven-3.9'
    // }

    parameters {
        //string(name: 'VERSION', defaultValue: '', description: 'version to deploy on prod')
        choice(name: 'VERSION', choices: ['1.1.0', '1.2.0', '1.3.0'], description: '')
        booleanParam(name: 'executeTests',  defaultValue: true, description: '')
    }

    stages {

        stage("build") {
            
            steps {
                echo "Building the application..."
               // echo "Building version is ${NEW_VERSION}"
            }

        }

        stage("test") {

            when {
                expression {
                    params.executeTests
                }
            }
            
            steps {
                echo "Testing the application..."
               // echo "Credentials is ${SERVER_CERDENTIALS}"
            }

        }

        stage("deploy") {
            
            steps {
                echo "Deploy the application..."
                echo "Deploying version ${params.VERSION}"

                // withCredentials([usernamePassword(
                //     credentials: 'test-in-file', 
                //     usernameVariable: USER, 
                //     passwordVariable: PWD) ])
                // {
                    
                //     sh "some script ${USER} ${PWD}"


                // }
            }

        }

    }
}