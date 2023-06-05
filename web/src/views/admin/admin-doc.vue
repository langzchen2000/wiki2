<template>
    <a-layout>
        <a-layout-content
            :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
        >
            <a-row :gutter="24">
                <a-col :span="6">
                    <p>
                        <a-form layout="inline">
                            <a-form-item>
                                <a-button type="primary" @click="add()">
                                    Add
                                </a-button>
                            </a-form-item>
                        </a-form>
                    </p>
                        <a-table
                            v-if="level1.length > 0"
                            :columns="columns"
                            :row-key="record => record.id"
                            :data-source="level1"
                            :loading="loading"
                            :pagination="false"
                            size="small"
                            :defaultExpandAllRows="true"
                        >
                                    <template #action="{ record }">
                                        <a-space size="small">
                                            <a-button type="primary" @click="edit(record)">
                                                Edit
                                            </a-button>
                                            <a-popconfirm
                                                title="Are you sure delete this task?"
                                                ok-text="Yes"
                                                cancel-text="No"
                                                @confirm="handleDelete(record.id)"
                                            >
                                                <a-button type="danger">
                                                    Delete
                                                </a-button>
                                            </a-popconfirm>

                                        </a-space>
                                    </template>
                        </a-table>
                </a-col>
                <a-col :span="16">
                    <p style="padding-left:60px">
                        <a-button type="primary" @click="handleSave()" >
                            Save
                        </a-button>
                    </P>

                    <a-form :model="doc">

                        <a-form-item label="name" style="padding-left:13px">
                            <a-input v-model:value="doc.name" />
                        </a-form-item>
                        <a-form-item label="parent" style="padding-left:7px">
                            <a-tree-select
                                v-model:value="doc.parent"
                                style="width: 100%"
                                :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
                                :tree-data="treeSelectData"
                                placeholder="Please select a parent doc"
                                tree-default-expand-all
                                :replaceFields="{title: 'name', key: 'id', value: 'id'}"
                            >
                            </a-tree-select>
                        </a-form-item>
                        <a-form-item label="sort" style="padding-left:23px">
                            <a-input v-model:value="doc.sort" />
                        </a-form-item>
                        <a-form-item label="content">

                            <div style="border: 1px solid #ccc">
                                <Toolbar
                                    style="border-bottom: 1px solid #ccc"
                                    :editor="editorRef"
                                    :defaultConfig="toolbarConfig"
                                    :mode="mode"
                                />
                                <Editor
                                    style="height: 200px; overflow-y: hidden;"
                                    v-model="valueHtml"
                                    :defaultConfig="editorConfig"
                                    :mode="mode"
                                    @onCreated="handleCreated"
                                />
                            </div>

                        </a-form-item>

                    </a-form>
                </a-col>
            </a-row>
        </a-layout-content>
    </a-layout>

</template>

<script lang="ts">
import { defineComponent, onMounted, ref } from 'vue';
import axios from 'axios';
import {message} from "ant-design-vue";
import {useRoute} from "vue-router";
import '@wangeditor/editor/dist/css/style.css' // 引入 css
import '@wangeditor/editor/dist/css/style.css' // 引入 css
import { onBeforeUnmount,  shallowRef } from 'vue'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'

export default defineComponent({
    name: 'AdminDoc',
    components: { Editor, Toolbar },
    setup() {
        const route = useRoute();
        const categories = ref();
        const loading = ref(false);

        const columns = [
            {
                title: 'name',
                dataIndex: 'name'
            },
            {
                title: 'Action',
                key: 'action',
                slots: { customRender: 'action' }
            }
        ];

        /**
         * 数据查询
         **/
        let level1 = ref();
        level1.value = [];
        const handleQuery = () => {
            loading.value = true;
            level1.value = [];
            axios.get("/doc/all").then((response) => {
                loading.value = false;
                const data = response.data;
                if(data.success){
                    categories.value = data.content;
                    level1.value = array2Tree(data.content, 0);
                } else {
                    message.error(data.message);
                }
            });
        };//onMounted 会在页面渲染之后执行, 点击页码也会触发

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
    }//performance: O(n^2)

        // -------- 表单 ---------
        const treeSelectData = ref();
        treeSelectData.value = [];
        const editorRef = shallowRef()
        // 内容 HTML
        const valueHtml = ref('<p></p>')
        const toolbarConfig = {}
        const editorConfig = { placeholder: '请输入内容...' }
        const doc = ref();
        doc.value = {};
        const modalVisible = ref(false);
        const modalLoading = ref(false);

        /**
         * 将某节点及其子孙节点全部置为disabled
         */
        const setDisable = (treeSelectData: any, id: any) => {
            // console.log(treeSelectData, id);
            // 遍历数组，即遍历某一层节点
            for (let i = 0; i < treeSelectData.length; i++) {
                const node = treeSelectData[i];
                if (node.id === id) {
                    // 如果当前节点就是目标节点
                    console.log("disabled", node);
                    // 将目标节点设置为disabled
                    node.disabled = true;

                    // 遍历所有子节点，将所有子节点全部都加上disabled
                    const children = node.children;
                    if (children !== undefined && children.length > 0) {
                        for (let j = 0; j < children.length; j++) {
                            setDisable(children, children[j].id)
                        }
                    }
                }
            }

        };

        // 组件销毁时，也及时销毁编辑器
        onBeforeUnmount(() => {
            const editor = editorRef.value
            if (editor == null) return
            editor.destroy()
        })

        const handleCreated = (editor: any) => {
            editorRef.value = editor // 记录 editor 实例，重要！
        }
        /**
         * 编辑
         */
        const handleQueryContent = () => {
            axios.get("/doc/content/" + doc.value.id)
                .then((response) => {
                const data = response.data;
                if (data.success) {
                    editorRef.value.setHtml(data.content);
                } else {
                    message.error(data.message);
                }
            });
        };
        const edit = (record: any) => {

            doc.value = Object.assign({}, record);
            handleQueryContent();
            treeSelectData.value = Object.assign([], level1.value);
            setDisable(treeSelectData.value, record.id);//将当前节点及其子孙节点全部置为disabled
            // 为选择树添加一个"无"
            treeSelectData.value.unshift({id: 0, name: 'null'});
        };

        const add = () => {
            treeSelectData.value = Object.assign([], level1.value);
            doc.value = {
                ebookId: route.query.ebookId
            };
        }

        const handleSave = () => {
            doc.value.content = editorRef.value.getHtml();
            axios.post("/doc/save", doc.value).then((response) => {
                modalLoading.value = false;
                const data = response.data; // data = commonResp
                if (data.success) {
                    // 重新加载列表
                    handleQuery();
                } else {
                    message.error(data.message);
                }
            });
        };

        /**
         * 查找整根树枝
         */
        const ids: Array<string> = [];
        const getDeleteIds = (treeSelectData: any, id: any) => {
            // console.log(treeSelectData, id);
            // 遍历数组，即遍历某一层节点
            for (let i = 0; i < treeSelectData.length; i++) {
                const node = treeSelectData[i];
                if (node.id === id) {
                    // 如果当前节点就是目标节点
                    console.log("delete", node);
                    // 将目标ID放入结果集ids
                    ids.push(id);
                    // 遍历所有子节点
                    const children = node.children;
                    if (children != undefined && children.length > 0){
                        for (let j = 0; j < children.length; j++) {
                            getDeleteIds(children, children[j].id)
                        }
                    }
                } else {
                    // 如果当前节点不是目标节点，则到其子节点再找找看。
                    const children = node.children;
                    if (children != undefined && children.length > 0) {
                        getDeleteIds(children, id);
                    }
                }
            }
        };

        const handleDelete = (id: number) => {
            ids.length = 0;
            getDeleteIds(level1.value, id);
            console.log(ids);
            axios.delete("/doc/delete/" + ids.join(",")).then((response) => {
                const data = response.data; // data = commonResp
                if (data.success) {
                    // 重新加载列表
                    handleQuery();
                }
            });
        };


        //删除按键
        const confirm = (e: MouseEvent) => {
            console.log(e);
            message.success('Click on Yes');
        };

        const cancel = (e: MouseEvent) => {
            console.log(e);
            message.error('Click on No');
        };

        onMounted(() => {
            handleQuery();
        });

        //search
        const searchKey = ref("");
        const search = () => {
            axios.get("/doc/list", {
                params: {
                    name: searchKey.value,
                    page: 1,
                    size: 4
                }
            }).then((response) => {
                const data = response.data;
                if (data.success) {
                    categories.value = data.content.list;
                } else {
                    message.error(data.message);
                }

            })
        }

        return {
            categories,
            columns,
            loading,
            handleQuery,

            edit,
            add,
            handleDelete,

            doc,
            modalVisible,
            modalLoading,

            confirm,
            cancel,

            searchKey,
            search,

            level1,
            treeSelectData,

            //editor
            editorRef,
            valueHtml,
            mode: 'default', // 或 'simple'
            toolbarConfig,
            editorConfig,
            handleCreated,
            handleSave

        }
    }
});
</script>

<style scoped>
img {
    width: 50px;
    height: 50px;
}
</style>
