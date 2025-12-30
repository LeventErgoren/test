// Jenkinsfile (Windows-friendly PowerShell steps)
// YDG stages: checkout -> build -> unit tests (report) -> integration tests (report)
// -> run on docker containers -> run selenium scenarios sequentially (report)

pipeline {
  agent any

  options {
    timestamps()
    disableConcurrentBuilds()
    skipDefaultCheckout(true)
    buildDiscarder(logRotator(numToKeepStr: '20'))
  }

  environment {
    MAVEN_OPTS = '-Dfile.encoding=UTF-8'
  }

  stages {
    stage('1-Checkout (GitHub)') {
      steps {
        checkout scm
      }
    }

    stage('2-Build') {
      steps {
        powershell(script: '''
          $ErrorActionPreference = "Stop"

          Write-Host "== Backend build (skip tests) =="
          Push-Location "demo"
          try {
            & .\\mvnw.cmd -B -DskipTests clean package
          } finally {
            Pop-Location
          }

          Write-Host "== Frontend build =="
          Push-Location "frontend"
          try {
            if (Test-Path "package-lock.json") {
              npm ci
            } else {
              npm install
            }
            npm run build
          } finally {
            Pop-Location
          }
        ''')
      }
    }

    stage('3-Unit Tests (Birim)') {
      steps {
        powershell(script: '''
          $ErrorActionPreference = "Stop"
          Push-Location "demo"
          try {
            & .\\mvnw.cmd -B "-Dtest=com.example.BirimTestleri.*" test
          } finally {
            Pop-Location
          }
        ''')
      }
      post {
        always {
          junit allowEmptyResults: true, testResults: 'demo/target/surefire-reports/*.xml'
        }
      }
    }

    stage('4-Integration Tests (Entegrasyon)') {
      steps {
        powershell(script: '''
          $ErrorActionPreference = "Stop"
          Push-Location "demo"
          try {
            & .\\mvnw.cmd -B "-Dtest=com.example.EntegrasyonTestleri.*" test
          } finally {
            Pop-Location
          }
        ''')
      }
      post {
        always {
          junit allowEmptyResults: true, testResults: 'demo/target/surefire-reports/*.xml'
        }
      }
    }

    stage('5-Run System on Docker') {
      steps {
        powershell(script: '''
          $ErrorActionPreference = "Stop"

          Write-Host "== docker compose up (build + detached) =="
          & docker compose -f docker-compose.yml up -d --build --wait
          if ($LASTEXITCODE -ne 0) {
            Write-Host "docker compose --wait not supported or failed; retrying without --wait"
            & docker compose -f docker-compose.yml up -d --build
            if ($LASTEXITCODE -ne 0) { throw "docker compose up failed (exit=$LASTEXITCODE)" }
          }

          Write-Host "== basic reachability checks (frontend+backend) =="
          $max = 30
          for ($i = 1; $i -le $max; $i++) {
            try {
              Invoke-WebRequest -UseBasicParsing -TimeoutSec 3 http://localhost:1313/ | Out-Null
              Invoke-WebRequest -UseBasicParsing -TimeoutSec 3 http://localhost:9090/ | Out-Null
              Write-Host "System is reachable."
              break
            } catch {
              if ($i -eq $max) { throw }
              Start-Sleep -Seconds 2
            }
          }
        ''')
      }
    }

    stage('6-Selenium Tests (UI Scenarios)') {
      steps {
        script {
          // Run selenium module tests using demo's Maven Wrapper
          def runSelenium = { String testSelector ->
            powershell(script: """
              \$ErrorActionPreference = 'Stop'
              .\\demo\\mvnw.cmd -B -f seleniumtestleri\\pom.xml "-Dtest=${testSelector}" test
            """)
          }

          // 6.1 BagimsizTestler
          stage('6.1-Selenium: BagimsizTestler') {
            runSelenium('BagimsizTestler.*')
          }

          // 6.2+ Senaryo folders: Senaryo1..SenaryoN
          def scenarioRoot = "${env.WORKSPACE}\\seleniumtestleri\\src\\test\\java"

          def scenarioFolders = powershell(returnStdout: true, script: """
            \$ErrorActionPreference = 'Stop'
            \$root = '${scenarioRoot}'
            Get-ChildItem -Path \$root -Directory |
              Where-Object { \$_.Name -match '^Senaryo\\d+\$' } |
              Sort-Object { [int]([regex]::Match(\$_.Name, '\\d+').Value) } |
              ForEach-Object { \$_.Name }
          """).trim().split(/\r?\n/).findAll { it?.trim() }

          for (def scenarioName : scenarioFolders) {
            def scenarioPath = "${scenarioRoot}\\${scenarioName}"

            // Collect .java file names in that scenario folder; sort by numeric TestN if present, else by name
            def classNames = powershell(returnStdout: true, script: """
              \$ErrorActionPreference = 'Stop'
              Get-ChildItem -Path '${scenarioPath}' -Filter '*.java' -File |
                Select-Object @{
                  Name='ClassName'; Expression={ [System.IO.Path]::GetFileNameWithoutExtension(\$_.Name) }
                }, @{
                  Name='Order'; Expression={
                    \$m = [regex]::Match(\$_.Name, 'Test(\\d+)')
                    if (\$m.Success) { [int]\$m.Groups[1].Value } else { 999999 }
                  }
                }, Name |
                Sort-Object Order, Name |
                ForEach-Object { \$_.ClassName }
            """).trim().split(/\r?\n/).findAll { it?.trim() }

            // Each test class as its own stage: ensures Test1 -> Test2 -> ...
            for (def className : classNames) {
              def fqcn = "${scenarioName}.${className}"
              stage("6.${scenarioName}-${className}") {
                runSelenium(fqcn)
              }
            }
          }
        }
      }
      post {
        always {
          junit allowEmptyResults: true, testResults: 'seleniumtestleri/target/surefire-reports/*.xml'
        }
      }
    }
  }

  post {
    always {
      // Final safety: publish all known JUnit results (does not fail if none)
      junit allowEmptyResults: true, testResults: 'demo/target/surefire-reports/*.xml, seleniumtestleri/target/surefire-reports/*.xml'

      // Always try to tear down containers (do not fail build if cleanup fails)
      powershell(script: '''
        try {
          docker compose -f docker-compose.yml down -v
        } catch {
          Write-Host "docker compose down failed: $($_.Exception.Message)"
        }
      ''')
    }
  }
}
