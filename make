cd ./out/
cp ../src/util/argparser.jar ./
jar -xvf argparser.jar
jar -cvfm cc0.jar ../src/META-INF/MANIFEST.MF ./