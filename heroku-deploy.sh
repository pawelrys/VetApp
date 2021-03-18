#!/bin/bash

IMAGE_ID=$(docker inspect registry.heroku.com/$1/web --format={{.Id}})
curl \
  -n \
  -X PATCH https://api.heroku.com/apps/$1/formation \
  -d '{"updates":[{"type":"web", "docker_image":"'"$IMAGE_ID"'"}]}' \
  -H "Content-Type:application/json" \
  -H "Accept:application/vnd.heroku+json; version=3.docker-releases" \
  -H "Authorization:Bearer $2"
