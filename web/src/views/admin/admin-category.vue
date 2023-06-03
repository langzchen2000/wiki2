<template>
    <a-layout>
        <a-layout-content
            :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
        >

            <a-button type="primary" @click="add" size="large">Add</a-button>

            <a-table
                :columns="columns"
                :row-key="record => record.id"
                :data-source="categories"
                :loading="loading"
                :pagination="false"
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
                            @confirm="handleDelete(record)"
                        >
                            <a-button type="danger">
                                Delete
                            </a-button>
                        </a-popconfirm>

                    </a-space>
                </template>
            </a-table>
        </a-layout-content>
    </a-layout>

    <a-modal
        title="category table"
        v-model:visible="modalVisible"
        :confirm-loading="modalLoading"
        @ok="handleModalOk"
    >
        <a-form :model="category" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">

            <a-form-item label="name">
                <a-input v-model:value="category.name" />
            </a-form-item>
            <a-form-item label="parent">
                <a-input v-model:value="category.parent" />
            </a-form-item>
            <a-form-item label="sort">
                <a-input v-model:value="category.sort" />
            </a-form-item>

        </a-form>
    </a-modal>
</template>

<script lang="ts">
import { defineComponent, onMounted, ref } from 'vue';
import axios from 'axios';
import {message} from "ant-design-vue";

export default defineComponent({
    name: 'AdminCategory',
    setup() {
        const categories = ref();
        const loading = ref(false);

        const columns = [
            {
                title: 'name',
                dataIndex: 'name'
            },
            {
                title: 'parent',
                key: 'parent',
                dataIndex: 'parent'
            },
            {
                title: 'sort',
                key: 'sort',
                dataIndex: 'sort'
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
        const handleQuery = () => {
            loading.value = true;
            axios.get("/category/all").then((response) => {
                loading.value = false;
                const data = response.data;
                if(data.success){
                    categories.value = data.content;
                } else {
                    message.error(data.message);
                }
            });
        };//onMounted 会在页面渲染之后执行, 点击页码也会触发


        // -------- 表单 ---------
        const category = ref({
            name: null,
            parent: null,
            sort: null
        });
        const modalVisible = ref(false);
        const modalLoading = ref(false);
        const handleModalOk = () => {
            modalLoading.value = true;
            axios.post("/category/save", category.value).then((response) => {
                modalLoading.value = false;
                const data = response.data; // data = commonResp
                if (data.success) {
                    modalVisible.value = false;

                    // 重新加载列表
                    handleQuery();
                } else {
                    message.error(data.message);
                }
            });
        };//表单提交

        /**
         * 编辑
         */
        const edit = (record: any) => {
            modalVisible.value = true;
            category.value = Object.assign({}, record);
        };

        const add = () => {
            category.value = {
                name: null,
                parent: null,
                sort: null
            };
            modalVisible.value = true;
        }


        const handleDelete = (record: any) => {
            axios.delete("/category/" + record.id).then((response) => {
                const data = response.data;
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
            axios.get("/category/list", {
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

            edit,
            add,
            handleDelete,

            category,
            modalVisible,
            modalLoading,
            handleModalOk,

            confirm,
            cancel,

            searchKey,
            search

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
