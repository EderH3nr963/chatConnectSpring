#!/bin/bash

BUCKET_NAME="chat-connect-app"
CHAT_QUEUE_NAME="s3-chat-media-events-queue"
USER_QUEUE_NAME="s3-user-avatar-events-queue"
REGION="us-east-1"

echo "=== Criando Fila SQS (Chat) ==="
awslocal sqs create-queue --queue-name $CHAT_QUEUE_NAME --region $REGION

CHAT_QUEUE_URL="http://localhost:4566/000000000000/$CHAT_QUEUE_NAME"
CHAT_QUEUE_ARN=$(awslocal sqs get-queue-attributes \
    --queue-url $CHAT_QUEUE_URL \
    --attribute-names QueueArn \
    --query "Attributes.QueueArn" \
    --output text \
    --region $REGION)

echo "ARN da Fila Chat: $CHAT_QUEUE_ARN"

echo "=== Criando Fila SQS (User Avatar) ==="
awslocal sqs create-queue --queue-name $USER_QUEUE_NAME --region $REGION

USER_QUEUE_URL="http://localhost:4566/000000000000/$USER_QUEUE_NAME"
USER_QUEUE_ARN=$(awslocal sqs get-queue-attributes \
    --queue-url $USER_QUEUE_URL \
    --attribute-names QueueArn \
    --query "Attributes.QueueArn" \
    --output text \
    --region $REGION)

echo "ARN da Fila User: $USER_QUEUE_ARN"

echo "=== Concedendo Permissões para o S3 Publicar nas Filas SQS ==="

CHAT_POLICY='{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": "*",
      "Action": "sqs:SendMessage",
      "Resource": "'"$CHAT_QUEUE_ARN"'",
      "Condition": {
        "ArnEquals": {
          "aws:SourceArn": "arn:aws:s3:::'"$BUCKET_NAME"'"
        }
      }
    }
  ]
}'

awslocal sqs set-queue-attributes \
    --queue-url $CHAT_QUEUE_URL \
    --attributes Policy="'$CHAT_POLICY'" \
    --region $REGION

USER_POLICY='{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": "*",
      "Action": "sqs:SendMessage",
      "Resource": "'"$USER_QUEUE_ARN"'",
      "Condition": {
        "ArnEquals": {
          "aws:SourceArn": "arn:aws:s3:::'"$BUCKET_NAME"'"
        }
      }
    }
  ]
}'

awslocal sqs set-queue-attributes \
    --queue-url $USER_QUEUE_URL \
    --attributes Policy="'$USER_POLICY'" \
    --region $REGION

echo "=== Criando Bucket S3 ==="
awslocal s3 mb s3://$BUCKET_NAME --region $REGION

echo "=== Desativando Bloqueio de Políticas Públicas do Bucket ==="

awslocal s3api put-public-access-block \
    --bucket $BUCKET_NAME \
    --public-access-block-configuration "BlockPublicAcls=false,IgnorePublicAcls=false,BlockPublicPolicy=false,RestrictPublicBuckets=false" \
    --region $REGION

echo "=== Aplicando Politicas de CORS ==="

CORS_CONFIGURATION='{
 "CORSRules": [
   {
     "AllowedOrigins": ["http://localhost:3000"],
     "AllowedMethods": ["PUT", "GET", "HEAD"],
     "AllowedHeaders": ["*"],
     "ExposeHeaders": ["ETag"],
     "MaxAgeSeconds": 3600
   }
 ]
}'

# Configuração CORS
awslocal s3api put-bucket-cors \
    --bucket $BUCKET_NAME \
    --cors-configuration "$CORS_CONFIGURATION" \
    --region $REGION

echo "=== Aplicando Política de Leitura Pública (apenas para avatares) ==="
BUCKET_POLICY='{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "PublicReadForUserAvatars",
      "Effect": "Allow",
      "Principal": "*",
      "Action": "s3:GetObject",
      "Resource": "arn:aws:s3:::'"$BUCKET_NAME"'/uploads/user/avatar/*"
    },
    {
      "Sid": "PublicReadForChatAvatars",
      "Effect": "Allow",
      "Principal": "*",
      "Action": "s3:GetObject",
      "Resource": "arn:aws:s3:::'"$BUCKET_NAME"'/uploads/chat/avatar/*"
    }
  ]
}'

awslocal s3api put-bucket-policy \
    --bucket $BUCKET_NAME \
    --policy "$BUCKET_POLICY" \
    --region $REGION

echo "=== Vinculando Notificação do S3 para as Filas SQS com Filtro por Pasta ==="
awslocal s3api put-bucket-notification-configuration \
    --bucket $BUCKET_NAME \
    --notification-configuration '{
        "QueueConfigurations": [
            {
                "Id": "EventoUploadMidiaChat",
                "QueueArn": "'"$CHAT_QUEUE_ARN"'",
                "Events": ["s3:ObjectCreated:*"],
                "Filter": {
                    "Key": {
                        "FilterRules": [
                            {
                                "Name": "prefix",
                                "Value": "uploads/chat/message/"
                            }
                        ]
                    }
                }
            },
            {
                "Id": "EventoUploadAvatarUser",
                "QueueArn": "'"$USER_QUEUE_ARN"'",
                "Events": ["s3:ObjectCreated:*"],
                "Filter": {
                    "Key": {
                        "FilterRules": [
                            {
                                "Name": "prefix",
                                "Value": "uploads/user/avatar/"
                            }
                        ]
                    }
                }
            }
        ]
    }' \
    --region $REGION

echo "=== Configuração Concluída ==="