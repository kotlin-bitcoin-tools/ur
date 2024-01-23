test:
  ./gradlew test

onetest TEST:
  ./gradlew test --tests {{TEST}}

serve:
  ./gradlew dokkaHtml && rm -rf ./docs/api/ && mv ./build/dokka/html ./docs/api && mkdocs serve

dokka:
   ./gradlew dokkaHtml
