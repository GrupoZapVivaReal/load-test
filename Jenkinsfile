node {
    stage 'Checkout from Github'
    checkout scm
    stage "${env.JOB_NAME}"

    withCredentials([
        string(credentialsId: 'SLK_TOKEN', variable: 'SLK_TOKEN'),
        string(credentialsId: 'K8S_SEARCH_TOKEN_' + env.ENV.toUpperCase(), variable: 'K8S_TOKEN'),
        string(credentialsId: 'CLUSTER_NAME_' + env.ENV.toUpperCase(), variable: 'CLUSTER_NAME')
    ]){
        def slkChannel = '-Dslack.channel=' + env.SLK_CHANNEL
        def apiHttpPath = env.API_HTTP_PATH != '' ? ' -Dapi.http.path=' + env.API_HTTP_PATH : ''
        def gatlingMaxduration = env.GATLING_MAXDURATION != '' ? ' -Dgatling.maxDuration=' + env.GATLING_MAXDURATION : ''
        def gatlingUsers = env.GATLING_USERS != '' ? ' -Dgatling.users=' + env.GATLING_USERS : ''
        def gatlingRepeat = env.GATLING_REPEAT != '' ? ' -Dgatling.repeat=' + env.GATLING_REPEAT : ''
        def gatlingReadtimeout = env.GATLING_READTIMEOUT != '' ? ' -Dgatling.readTimeout=' + env.GATLING_READTIMEOUT : ''
        def gatlingConntimeout = env.GATLING_CONNTIMEOUT != '' ? ' -Dgatling.connTimeout=' + env.GATLING_CONNTIMEOUT : ''
        def gatlingIncludeScenarios = env.GATLING_INCLUDE_SCENARIOS != '' ? ' -Dgatling.includeScenarios=' + env.GATLING_INCLUDE_SCENARIOS : ''
        def gatlingExcludeScenarios = env.GATLING_EXCLUDE_SCENARIOS != '' ? ' -Dgatling.excludeScenarios=' + env.GATLING_EXCLUDE_SCENARIOS : ''

        def s3Bucket = env.S3_BUCKET != '' ? ' -Daws.s3.bucket=' + env.S3_BUCKET : ''
        def s3Path = env.S3_PATH != '' ? ' -Dscenarios.s3.path=' + env.S3_PATH : ''

        def ltExtraArgs=(slkChannel + apiHttpPath + gatlingMaxduration + gatlingUsers + gatlingRepeat + gatlingReadtimeout + gatlingConntimeout + s3Bucket + s3Path + gatlingIncludeScenarios + gatlingExcludeScenarios)

        def targetEndpoint = env.LT_ENDPOINT
        def k8s_cluster = env.K8S_CLUSTER.replace("*env*", env.ENV)

        def command = 'make K8S_CLUSTER=' + k8s_cluster +  ' LT_ENDPOINT=' + targetEndpoint +' LT_EXTRA_ARGS="' + ltExtraArgs + '" deploy'
        def result = sh(script: command, returnStatus: true)

        sendToSlack(result, env.SLK_CHANNEL, targetEndpoint, ltExtraArgs)
    }
}

def sendToSlack(result, slkChannel, targetEndpoint, ltExtraArgs) {
    def color = 'good'
    def status = 'SUCCESS'
    if (result != 0) {
        color = 'danger'
        status = 'ERROR'
    }
    def deployMessage = "Deployment on :k8s: for *${env.JOB_NAME}* realized with *${status}*.\n*Target:* ${targetEndpoint}\n*Extra arguments configuration:* ${ltExtraArgs}"
    slackSend channel: "${slkChannel}", color: color, message: "Job <${env.BUILD_URL}/console|#${env.BUILD_NUMBER}> *${env.JOB_NAME}*, in *${env.AWS_DEFAULT_REGION}* with *${env.ENV}*.\n${deployMessage}", teamDomain: 'vivareal'
}