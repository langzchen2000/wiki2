<template>
    <a-layout>
        <a-layout-content :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }">
            <a-row>
                <a-col :span="6">
                    <a-tree
                        v-if="level1.length > 0"
                        :tree-data="level1"
                        :replaceFields="{title: 'name', key: 'id', value: 'id'}"
                        :defaultExpandAll="true"
                        @select="onSelect"
                    >
                    </a-tree>
                </a-col>
                <a-col :span="18">
                    <div class="wangeditor" :innerHTML="html"></div>
                </a-col>
            </a-row>
        </a-layout-content>
    </a-layout>
</template>


<script lang="ts">
    import { defineComponent, onMounted, ref, createVNode } from 'vue';
    import axios from 'axios';
    import {message} from 'ant-design-vue';
    import {useRoute} from "vue-router";

    export default defineComponent({
        name: 'AdminDoc',
        setup() {
            const route = useRoute();
            const docs = ref();
            const level1 = ref(); // 一级文档树，children属性就是二级文档
            level1.value = [];
            const html = ref();

        /**
         * 数据查询
         **/
        const handleQuery = () => {
        axios.get("/doc/all/" + route.query.ebookId).then((response) => {
                const data = response.data;
                if (data.success) {
                    docs.value = data.content;

                    level1.value = [];
                    level1.value = array2Tree(docs.value, 0);
                } else {
                    message.error(data.message);
                }
            });
        };

        const handleQueryContent = (id: number) => {
            axios.get("/doc/content/" + id).then((response) => {
                const data = response.data;
                if (data.success) {
                    html.value = data.content;
                } else {
                    message.error(data.message);
                }
            });
        };

        const onSelect = (selectedKeys: any, info: any) => {
            if (selectedKeys !== undefined && selectedKeys.length > 0) {
                // 加载内容
                handleQueryContent(selectedKeys[0]);
            }
        };
        const array2Tree = (array: any, parentId: number) => {
            if (array.length === 0) {
                return [];
            }
            const result = [];
            for (let i = 0; i < array.length; i++) {
                const c = array[i];
                // console.log(Number(c.parent), Number(parentId));
                if (Number(c.parent) === Number(parentId)) {
                    result.push(c);
                    // 递归查看当前节点对应的子节点
                    const children = array2Tree(array, c.id);
                    if (children.length > 0) {
                        c.children = children;
                    }
                }
            }
            return result;
        }
    onMounted(() => {
        handleQuery();
    });

            return {
                level1,
                html,
                onSelect
            }
        }
    });
</script>


<style>
/* wangeditor默认样式, 参照: http://www.wangeditor.com/doc/pages/02-%E5%86%85%E5%AE%B9%E5%A4%84%E7%90%86/03-%E8%8E%B7%E5%8F%96html.html */
/* table 样式 */
    .wangeditor table {
        border-top: 1px solid #ccc;
        border-left: 1px solid #ccc;
    }
    .wangeditor table td,
    .wangeditor table th {
        border-bottom: 1px solid #ccc;
        border-right: 1px solid #ccc;
        padding: 3px 5px;
    }
    .wangeditor table th {
        border-bottom: 2px solid #ccc;
        text-align: center;
    }

    /* blockquote 样式 */
    .wangeditor blockquote {
        display: block;
        border-left: 8px solid #d0e5f2;
        padding: 5px 10px;
        margin: 10px 0;
        line-height: 1.4;
        font-size: 100%;
        background-color: #f1f1f1;
    }

    /* code 样式 */
    .wangeditor code {
        display: inline-block;
        *display: inline;
        *zoom: 1;
        background-color: #f1f1f1;
        border-radius: 3px;
        padding: 3px 5px;
        margin: 0 3px;
    }
    .wangeditor pre code {
        display: block;
    }

    /* ul ol 样式 */
    .wangeditor ul, ol {
        margin: 10px 0 10px 20px;
    }
</style>