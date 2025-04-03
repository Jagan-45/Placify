package com.murali.placify.util;

import org.springframework.stereotype.Component;

@Component
public class SourceCodeModifier {

    public String addImports(int langId, String code) {
        if (langId == 54)
            return addCppImports(code);
         else if (langId == 62)
            return addJavaImports(code);
         else return addPythonImports(code);
    }

    private String addPythonImports(String code) {
        String imports = "import sys\n" +
                "import math\n" +
                "import collections\n" +
                "import heapq\n" +
                "import itertools\n" +
                "import functools\n" +
                "import bisect\n" +
                "import operator\n" +
                "import re\n";

        return imports + code;
    }

    private String addJavaImports(String code) {
        String imports = "import java.util.*;\n" +
                "import java.io.*;\n" +
                "import java.math.*;\n" +
                "import java.util.regex.*;\n" +
                "import java.io.BufferedReader;\n" +
                "import java.io.InputStreamReader;\n" +
                "import java.io.PrintWriter;\n" +
                "import java.util.StringTokenizer;\n";

        return imports + code;
    }

    private String addCppImports(String code) {
        String imports = "#include <iostream>\n" +
                "#include <vector>\n" +
                "#include <string>\n" +
                "#include <algorithm>\n" +
                "#include <cmath>\n" +
                "#include <map>\n" +
                "#include <set>\n" +
                "#include <queue>\n" +
                "#include <stack>\n" +
                "#include <unordered_map>\n" +
                "#include <unordered_set>\n" +
                "#include <deque>\n" +
                "#include <bitset>\n" +
                "#include <numeric>\n" +
                "#include <sstream>\n" +
                "using namespace std;\n";

        return imports + code;
    }
}
