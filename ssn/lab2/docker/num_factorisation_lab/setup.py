#!/usr/bin/python

from distutils.core import setup, Extension
from Cython.Build import cythonize

setup( ext_modules = cythonize('num_factorisation_lab.pyx') )
