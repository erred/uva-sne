FROM python:2-alpine

WORKDIR /workspace
RUN apk update && apk upgrade && apk add bash gcc musl-dev && pip install cython
COPY num_factorisation_lab .
RUN python setup.py build_ext --inplace

ENTRYPOINT ["python", "num_factorisation_lab.py"]
