#!/bin/sh

./docgen.pl
pdflatex -synctex=1 -interaction=nonstopmode api.tex
rm -f api.aux api.log api.synctex.gz
