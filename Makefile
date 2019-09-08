.PHONY: mount unmount
mount:
	docker run --rm -dit --privileged --name doku-fuse --device /dev/fuse -v $$(pwd)/wiki:/home/user/doku/wiki:shared docker.pkg.github.com/seankhliao/uva-sne-courses/wiki-fuse:latest

unmount:
	docker stop doku-fuse
