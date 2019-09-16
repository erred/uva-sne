.PHONY:
mount:
<<<<<<< HEAD
	docker run --rm -it --privileged --name doku-fuse --device /dev/fuse -v $$(pwd)/wiki:/home/user/doku/wiki:shared docker.pkg.github.com/seankhliao/uva-sne-courses/wiki-fuse:latest

unmount:
	docker stop doku-fuse
=======
	docker run --rm -it --privileged --name doku-fuse --network host --device /dev/fuse -v $$(pwd)/wiki:/home/user/doku/wiki:shared docker.pkg.github.com/seankhliao/uva-sne-courses/wiki-fuse:latest
>>>>>>> parent of ce0ec6e3... remove host networking
