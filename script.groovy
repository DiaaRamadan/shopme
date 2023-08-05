def buildApp() {
    echo 'build the application'
}

def testApp() {
    echo 'Testing the application...'

}

def deployApp() {
    echo 'Deploy the application...'
    echo "Deploying version ${params.VERSION}"
}

return this
