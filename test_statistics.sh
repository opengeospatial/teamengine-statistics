#!/usr/bin/env bash

BASEDIR=$(dirname "$0")

printHelp(){

  echo "Usage: test_statistics [-u users directory and -c config.xml file]"
  echo "                        (to generate Statistics report)"
  echo "    -u	  Local path to users directory."
  echo ""
  echo "    -c	  Local path to config.xml file."
  echo ""
  echo " Examples:"
  echo "    ./test_statistics.sh -u ~/TE_BASE/users -c ~/TE_Base/config.xml"
  echo "" 
  echo "See https://github.com/opengeospatial/teamengine-statistics/"
  
  exit 0
}


if [ "$1" = "-h" -o "$1" = "--help" ]; then
  printHelp

fi

while [ "$1" ]; do
  key="$1"

  case $key in
      -u)
      users_dir="$2"
      shift
      ;;
      -c)
      config_file="$2"
      shift
      ;;
  esac
  shift
done  
  

if [ $users_dir ]; then
    users_folder=$(realpath $users_dir)
    echo "[INFO] Using the users directory: " $users_folder
  else
     echo "[FAIL] Please provide users diretctory path." $users_folder
     exit 0
fi


if [ $config_file ]; then   
    config_file_path=$config_file
    echo "[INFO] Using the config.xml file: " $config_file_path
  else
     echo "[FAIL] Please provide config.xml file path." $config_file_path
     exit 0
fi


cp=$BASEDIR/target

  for y in $BASEDIR/target/*deps.jar
  do
    cp=$cp:$y
  done

java -cp $cp org.opengis.te.stats.StatisticsReport -usersDir="$users_folder" -configFile="$config_file_path"


