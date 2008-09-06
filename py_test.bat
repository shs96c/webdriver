cd bindings\py\src

set PYTHONPATH=%PYTHONPATH%;.;..\lib 

python webdriver/unittests/basic_tests.py
cd ..\..\..