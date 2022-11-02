from unittest import TestCase
from Lambda.LambdaFunction import patternMatch

class Test(TestCase):
    def test_pattern(self):
        #Assert if the pattern is matched or not
        self.assertEqual(patternMatch("([a-c][e-g][0-3]|[A-Z][5-9][f-w]){5,15}", '34'),False)

