FROM gradle:8.5.0-jdk21
WORKDIR /app
COPY . /app

RUN gradle --no-daemon --warning-mode all --console=plain build

EXPOSE 8080
CMD ["sh", "start.sh"]