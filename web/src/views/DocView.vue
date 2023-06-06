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
                    >
                    </a-tree>
                </a-col>
                <a-col :span="18">
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
            }
        }
    });
</script>


<style scoped>

</style>