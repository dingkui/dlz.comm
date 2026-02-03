#!/bin/bash
# JDK 兼容性详细分析脚本

echo "=== JDK 兼容性详细分析报告 ==="
echo "生成时间: $(date)"
echo

# 1. 基础环境信息
echo "1. 基础环境信息"
echo "=================="
echo "Java版本:"
java -version 2>&1
echo
echo "Maven版本:"
mvn -v | grep "Apache Maven"
echo
echo "操作系统:"
mvn -v | grep "OS name"
echo

# 2. 项目配置分析
echo "2. 项目配置分析"
echo "=================="
echo "当前编译配置:"
mvn help:effective-pom | grep -A 2 "maven.compiler"
echo

# 3. 依赖兼容性检查
echo "3. 依赖兼容性检查"
echo "=================="
echo "核心依赖版本:"
mvn dependency:tree | grep -E "(jackson|lombok|slf4j)" | head -10
echo

# 4. 编译兼容性测试
echo "4. 编译兼容性测试"
echo "=================="

# 测试当前配置
echo "当前配置编译测试:"
mvn clean compile -q && echo "✓ 通过" || echo "✗ 失败"
echo

# 5. 警告和错误分析
echo "5. 编译警告分析"
echo "=================="
echo "Deprecated API 使用:"
mvn clean compile -X 2>&1 | grep -i "deprecated" | head -5
echo

echo "Unchecked 操作:"
mvn clean compile -X 2>&1 | grep -i "unchecked" | head -5
echo

# 6. 性能基准测试
echo "6. 性能基准测试"
echo "=================="
echo "编译时间测试:"
time mvn clean compile -q
echo

# 7. 兼容性建议
echo "7. 兼容性建议"
echo "=================="
echo "✅ 推荐的JDK版本: 8, 11, 17"
echo "⚠️  注意事项:"
echo "   - Lombok 1.18.30 支持 JDK 8-17"
echo "   - Jackson 2.13.4 支持 JDK 8+"
echo "   - SLF4J 1.7.32 支持 JDK 8+"
echo

echo "=== 分析完成 ==="