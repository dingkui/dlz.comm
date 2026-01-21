package com.dlz.comm.util;

/**
 * 通用值容器类，用于封装多个相关值的对象
 * 提供了从2个值到5个值的容器实现
 * 
 * @param <V1> 第一个值的类型
 * @param <V2> 第二个值的类型
 * 
 * @author dingkui
 * @since 2023
 */
public class VAL<V1, V2> {
    /** 第一个值 */
    public final V1 v1;
    /** 第二个值 */
    public final V2 v2;

    /**
     * 私有构造函数，创建包含两个值的容器
     * 
     * @param v1 第一个值
     * @param v2 第二个值
     */
    private VAL(V1 v1, V2 v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    /**
     * 三个值的容器类
     * 
     * @param <V1> 第一个值的类型
     * @param <V2> 第二个值的类型
     * @param <V3> 第三个值的类型
     */
    public static class VAL3<V1, V2, V3> extends VAL<V1, V2> {
        /** 第三个值 */
        public final V3 v3;

        /**
         * 创建包含三个值的容器
         * 
         * @param v1 第一个值
         * @param v2 第二个值
         * @param v3 第三个值
         */
        private VAL3(V1 v1, V2 v2, V3 v3) {
            super(v1, v2);
            this.v3 = v3;
        }

        /**
         * 返回包含三个值的字符串表示
         * 
         * @return 三个值的字符串表示
         */
        @Override
        public String toString() {
            return super.toString() + ",v3:" + v3;
        }
    }

    /**
     * 四个值的容器类
     * 
     * @param <V1> 第一个值的类型
     * @param <V2> 第二个值的类型
     * @param <V3> 第三个值的类型
     * @param <V4> 第四个值的类型
     */
    public static class VAL4<V1, V2, V3, V4> extends VAL3<V1, V2, V3> {
        /** 第四个值 */
        public final V4 v4;

        /**
         * 创建包含四个值的容器
         * 
         * @param v1 第一个值
         * @param v2 第二个值
         * @param v3 第三个值
         * @param v4 第四个值
         */
        private VAL4(V1 v1, V2 v2, V3 v3, V4 v4) {
            super(v1, v2, v3);
            this.v4 = v4;
        }

        /**
         * 返回包含四个值的字符串表示
         * 
         * @return 四个值的字符串表示
         */
        @Override
        public String toString() {
            return super.toString() + ",v4:" + v4;
        }
    }

    /**
     * 五个值的容器类
     * 
     * @param <V1> 第一个值的类型
     * @param <V2> 第二个值的类型
     * @param <V3> 第三个值的类型
     * @param <V4> 第四个值的类型
     * @param <V5> 第五个值的类型
     */
    public static class VAL5<V1, V2, V3, V4, V5> extends VAL4<V1, V2, V3, V4> {
        /** 第五个值 */
        public final V5 v5;

        /**
         * 创建包含五个值的容器
         * 
         * @param v1 第一个值
         * @param v2 第二个值
         * @param v3 第三个值
         * @param v4 第四个值
         * @param v5 第五个值
         */
        private VAL5(V1 v1, V2 v2, V3 v3, V4 v4, V5 v5) {
            super(v1, v2, v3, v4);
            this.v5 = v5;
        }

        /**
         * 返回包含五个值的字符串表示
         * 
         * @return 五个值的字符串表示
         */
        @Override
        public String toString() {
            return super.toString() + ",v5:" + v5;
        }
    }

    /**
     * 创建包含两个值的容器实例
     * 
     * @param <V1> 第一个值的类型
     * @param <V2> 第二个值的类型
     * @param v1 第一个值
     * @param v2 第二个值
     * @return 包含两个值的容器实例
     */
    public static <V1, V2> VAL<V1, V2> of(V1 v1, V2 v2) {
        return new VAL<>(v1, v2);
    }

    /**
     * 创建包含三个值的容器实例
     * 
     * @param <V1> 第一个值的类型
     * @param <V2> 第二个值的类型
     * @param <V3> 第三个值的类型
     * @param v1 第一个值
     * @param v2 第二个值
     * @param v3 第三个值
     * @return 包含三个值的容器实例
     */
    public static <V1, V2, V3> VAL3<V1, V2, V3> of(V1 v1, V2 v2, V3 v3) {
        return new VAL3<>(v1, v2, v3);
    }

    /**
     * 创建包含四个值的容器实例
     * 
     * @param <V1> 第一个值的类型
     * @param <V2> 第二个值的类型
     * @param <V3> 第三个值的类型
     * @param <V4> 第四个值的类型
     * @param v1 第一个值
     * @param v2 第二个值
     * @param v3 第三个值
     * @param v4 第四个值
     * @return 包含四个值的容器实例
     */
    public static <V1, V2, V3, V4> VAL4<V1, V2, V3, V4> of(V1 v1, V2 v2, V3 v3, V4 v4) {
        return new VAL4<>(v1, v2, v3, v4);
    }

    /**
     * 创建包含五个值的容器实例
     * 
     * @param <V1> 第一个值的类型
     * @param <V2> 第二个值的类型
     * @param <V3> 第三个值的类型
     * @param <V4> 第四个值的类型
     * @param <V5> 第五个值的类型
     * @param v1 第一个值
     * @param v2 第二个值
     * @param v3 第三个值
     * @param v4 第四个值
     * @param v5 第五个值
     * @return 包含五个值的容器实例
     */
    public static <V1, V2, V3, V4, V5> VAL5<V1, V2, V3, V4, V5> of(V1 v1, V2 v2, V3 v3, V4 v4, V5 v5) {
        return new VAL5<>(v1, v2, v3, v4, v5);
    }

    /**
     * 返回包含两个值的字符串表示
     * 
     * @return 两个值的字符串表示
     */
    @Override
    public String toString() {
        return "v1:" + v1 + ",v2:" + v2;
    }
}