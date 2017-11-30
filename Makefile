include make/git/Makefile

ORG:=vivareal

PROJECT_NAME:=load-test
include make/pro/Makefile

ARTIFACT:=$(PROJECT_NAME)-$(VERSION)

DOCKER_REGISTRY_DOMAIN=prod-search-docker-registry.vivareal.com

CONTAINER_ID:=$(VERSION)
IMAGE_NAME:=$(ORG)/search-api-v2:$(PROJECT_NAME)
include make/doc/Makefile

REPORT_VERSION?=$(VERSION)

artifact: ;

build: image ;

run-local: endpoint build
	@docker run --rm \
			-e AWS_ACCESS_KEY_ID=$(AWS_ACCESS_KEY_ID) \
			-e AWS_SECRET_ACCESS_KEY=$(AWS_SECRET_ACCESS_KEY) \
			-e REPORT_VERSION=$(REPORT_VERSION) \
			-e SLK_TOKEN=$SLK_TOKEN \
			$(IMAGE_NAME) -Dapi.http.base=$(LT_ENDPOINT) $(LT_EXTRA_ARGS)

endpoint:
	$(if $(value LT_ENDPOINT),,$(error "LT_ENDPOINT is required for Makefile"))

ENV:=prod
DEPLOY_NAME?=$(PROJECT_NAME)-$(COMMIT_HASH)
RUN_OPTS?=-Xmx1900m -Xms1900m -XX:MaxMetaspaceSize=1900m -Dapi.http.base=$(LT_ENDPOINT) $(LT_EXTRA_ARGS)
include make/k8s/Makefile

run: endpoint process-templates-inline run-k8s
