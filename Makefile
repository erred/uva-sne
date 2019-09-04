.PHONY:
mount:
	docker run --rm -it --network host --device /dev/fuse --privileged -v $(pwd)/wiki:/home/user/doku/wiki:shared --name doku-fuse docker.pkg.github.com/seankhliao/uva-sne-courses/wiki-fuse:latest
