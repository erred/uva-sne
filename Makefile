.PHONY:
mount:
	docker run --rm -it --privileged --name doku-fuse --network host --device /dev/fuse -v $$(pwd)/wiki:/home/user/doku/wiki:shared docker.pkg.github.com/seankhliao/uva-sne-courses/wiki-fuse:latest

unmount:
	docker stop doku-fuse
