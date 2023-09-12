echo " *** Build && Run Docker Image for the application *** "
docker stop terronapi && docker rm terronapi && docker build -t terronapi_img . && docker run -d -p 8000:8000 --name=terronapi terronapi_img
# if you want to hardcode the build artifact location with the build command, run:
# docker build --build-arg JAR_FILE=web/target/*.jar -t app-image .