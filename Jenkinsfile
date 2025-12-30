// Jenkinsfile (Windows-friendly PowerShell steps)
// Stages required by YDG: checkout -> build -> unit tests(report) -> integration tests(report)
// -> run on docker containers -> run selenium scenarios sequentially(report)

pipeline {
  agent any

  options {
    timestamps()
    disableConcurrentBuilds()
    skipDefaultCheckout(true)
    buildDiscarder(logRotator(numToKeepStr: '20'))
  }

  environment {
    // Keep logs consistent on Windows
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
        powershell '''
          $ErrorActionPreference = "Stop"

          Write-Host "== Backend build (skip tests) =="
          Push-Location "demo"
          try {
            .\mvnw.cmd -B -DskipTests clean package
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
        '''
      }
    }

    stage('3-Unit Tests (Birim)') {
      steps {
        powershell '''
          $ErrorActionPreference = "Stop"
          Push-Location "demo"
          try {
            # Run only unit tests in com.example.BirimTestleri
            .\mvnw.cmd -B -Dtest=com.example.BirimTestleri.* test
          } finally {
            Pop-Location
          }
        '''
      }
      post {
        always {
          // Surefire XML reports
          junit allowEmptyResults: true, testResults: 'demo/target/surefire-reports/*.xml'
        }
      }
    }

    stage('4-Integration Tests (Entegrasyon)') {
      steps {
        powershell '''
          $ErrorActionPreference = "Stop"
          Push-Location "demo"
          try {
            # Run only integration tests in com.example.EntegrasyonTestleri
            .\mvnw.cmd -B -Dtest=com.example.EntegrasyonTestleri.* test
          } finally {
            Pop-Location
          }
        '''
      }
      post {
        always {
          junit allowEmptyResults: true, testResults: 'demo/target/surefire-reports/*.xml'
        }
      }
    }

    stage('5-Run System on Docker') {
      steps {
        powershell '''
          $ErrorActionPreference = "Stop"

          Write-Host "== docker compose up (build + detached) =="
          # Prefer --wait if docker compose supports it; fall back otherwise.
          docker compose -f docker-compose.yml up -d --build --wait
          if ($LASTEXITCODE -ne 0) {
            Write-Host "docker compose --wait not supported or failed; retrying without --wait"
            docker compose -f docker-compose.yml up -d --build
          }

          Write-Host "== basic health checks =="
          $max = 30
          for ($i=1; $i -le $max; $i++) {
            try {
              Invoke-WebRequest -UseBasicParsing -TimeoutSec 3 http://localhost:1313/ | Out-Null
              Invoke-WebRequest -UseBasicParsing -TimeoutSec 3 http://localhost:9090/ | Out-Null
              Write-Host "System is reachable (frontend+backend)."
              break
            } catch {
              if ($i -eq $max) { throw }
              Start-Sleep -Seconds 2
            }
          }
        '''
      }
    }

    stage('6-Selenium Tests (UI Scenarios)') {
      steps {
        script {
          // Helper: run maven surefire for a specific test class or pattern
          def runSelenium = { String testSelector ->
            powershell """
              \$ErrorActionPreference = 'Stop'
              # Use backend Maven Wrapper so the agent doesn't need global Maven installed
              .\\demo\\mvnw.cmd -B -f seleniumtestleri/pom.xml -Dtest=${testSelector} test
            """
          }

          // 6.1 BagimsizTestler (all in one stage)
          stage('6.1-Selenium: BagimsizTestler') {
            runSelenium('BagimsizTestler.*')
            junit allowEmptyResults: true, testResults: 'seleniumtestleri/target/surefire-reports/*.xml'
          }

          // 6.2+ Senaryo folders (Senaryo1..SenaryoN) discovered from filesystem, each test class as its own stage
          def scenarioRoot = "${env.WORKSPACE}\\seleniumtestleri\\src\\test\\java"
          def scenarioFolders = powershell(returnStdout: true, script: """
            \$ErrorActionPreference = 'Stop'
            \$root = '${scenarioRoot}'
            Get-ChildItem -Path \$root -Directory | Where-Object { \$_.Name -like 'Senaryo*' } |
              Sort-Object { [int]([regex]::Match(\$_.Name, '\\d+').Value) } |
              ForEach-Object { \$_.Name }
          """).trim().split(/\r?\n/).findAll { it?.trim() }

          for (def scenarioName : scenarioFolders) {
            def scenarioPath = "${scenarioRoot}\\${scenarioName}"

            // collect test class names in that scenario folder, sorted by filename
            def classNames = powershell(returnStdout: true, script: """
              \$ErrorActionPreference = 'Stop'
              Get-ChildItem -Path '${scenarioPath}' -Filter '*.java' -File |
                Sort-Object Name |
                ForEach-Object { [System.IO.Path]::GetFileNameWithoutExtension(\$_.Name) }
            """).trim().split(/\r?\n/).findAll { it?.trim() }

            for (def className : classNames) {
              stage("6.x-Selenium: ${scenarioName} - ${className}") {
                runSelenium("${scenarioName}.${className}")
                junit allowEmptyResults: true, testResults: 'seleniumtestleri/target/surefire-reports/*.xml'
              }
            }
          }
        }
      }
    }
  }

  post {
    always {
      // Always try to tear down containers (do not fail build if cleanup fails)
      powershell '''
        try {
          docker compose -f docker-compose.yml down -v
        } catch {
          Write-Host "docker compose down failed: $($_.Exception.Message)"
        }
      '''
    }
  }
}
