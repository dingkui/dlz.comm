@echo off
echo ========================================
echo JDK 兼容性测试脚本
echo ========================================

echo 当前环境信息:
java -version
echo.
mvn -v
echo.

echo ========================================
echo 1. 测试 JDK 8 兼容性
echo ========================================
mvn clean compile -Pjdk8-test
if %ERRORLEVEL% NEQ 0 (
    echo ❌ JDK 8 编译失败
    goto :eof
) else (
    echo ✅ JDK 8 编译成功
)

echo ========================================
echo 2. 运行 JDK 8 单元测试
echo ========================================
mvn test -Pjdk8-test
if %ERRORLEVEL% NEQ 0 (
    echo ❌ JDK 8 测试失败
) else (
    echo ✅ JDK 8 测试成功
)

echo ========================================
echo 3. 测试 JDK 11 兼容性
echo ========================================
mvn clean compile -Pjdk11-test
if %ERRORLEVEL% NEQ 0 (
    echo ❌ JDK 11 编译失败
) else (
    echo ✅ JDK 11 编译成功
)

echo ========================================
echo 4. 运行 JDK 11 单元测试
echo ========================================
mvn test -Pjdk11-test
if %ERRORLEVEL% NEQ 0 (
    echo ❌ JDK 11 测试失败
) else (
    echo ✅ JDK 11 测试成功
)

echo ========================================
echo 5. 测试 JDK 17 兼容性
echo ========================================
mvn clean compile -Pjdk17-test
if %ERRORLEVEL% NEQ 0 (
    echo ❌ JDK 17 编译失败
) else (
    echo ✅ JDK 17 编译成功
)

echo ========================================
echo 6. 运行 JDK 17 单元测试
echo ========================================
mvn test -Pjdk17-test
if %ERRORLEVEL% NEQ 0 (
    echo ❌ JDK 17 测试失败
) else (
    echo ✅ JDK 17 测试成功
)

echo ========================================
echo 7. 测试 JDK 21 兼容性
echo ========================================
mvn clean compile -Pjdk21-test
if %ERRORLEVEL% NEQ 0 (
    echo ❌ JDK 21 编译失败
) else (
    echo ✅ JDK 21 编译成功
)

echo ========================================
echo 8. 运行 JDK 21 单元测试
echo ========================================
mvn test -Pjdk21-test
if %ERRORLEVEL% NEQ 0 (
    echo ❌ JDK 21 测试失败
) else (
    echo ✅ JDK 21 测试成功
)

echo ========================================
兼容性测试完成！
echo ========================================