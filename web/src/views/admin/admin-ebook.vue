<template>
    <a-layout>
        <a-layout-content
            :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
        >
            <a-input
                v-model:value="searchKey"
                placeholder="input search text"
                style="width: 200px; margin-right: 8px"

            />

                <a-button type="primary" @click="search" size="large" style="margin-right:8px">Search</a-button>


                <a-button type="primary" @click="add" size="large">Add</a-button>

            <a-table
                :columns="columns"
                :row-key="record => record.id"
                :data-source="ebooks"
                v-model:pagination="pagination"
                :loading="loading"
                @change="handleTableChange"
            >   <!-- record is an item of data source, @change pass a new pagination object to handleTableChange-->
                <template #cover="{ text: cover }">
                    <img v-if="cover" :src="cover" alt="avatar" />
                </template>
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
        title="电子书表单"
        v-model:visible="modalVisible"
        :confirm-loading="modalLoading"
        @ok="handleModalOk"
    >
        <a-form :model="ebook" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
            <a-form-item label="封面">
                <a-input v-model:value="ebook.cover" />
            </a-form-item>
            <a-form-item label="名称">
                <a-input v-model:value="ebook.name" />
            </a-form-item>
            <a-form-item label="分类">
                <a-cascader
                    v-model:value="categoryIds"
                    :field-names="{ label: 'name', value: 'id', children: 'children' }"
                    :options="level1"
                />
            </a-form-item>
            <a-form-item label="描述">
                <a-input v-model:value="ebook.desc" type="textarea" />
            </a-form-item>
        </a-form>
    </a-modal>
</template>

<script lang="ts">
import { defineComponent, onMounted, ref } from 'vue';
import axios from 'axios';
import {message} from "ant-design-vue";

export default defineComponent({
    name: 'AdminEbook',
    setup() {
        const ebooks = ref();
        const pagination = ref({
            current: 1,
            pageSize: 4,
            total: 0
        });
        const loading = ref(false);

        const columns = [
            {
                title: '封面',
                dataIndex: 'cover',
                slots: { customRender: 'cover' }
            },
            {
                title: '名称',
                dataIndex: 'name'
            },
            {
                title: '分类一',
                key: 'category1Id',
                dataIndex: 'category1Id'
            },
            {
                title: '分类二',
                dataIndex: 'category2Id'
            },
            {
                title: '文档数',
                dataIndex: 'docCount'
            },
            {
                title: '阅读数',
                dataIndex: 'viewCount'
            },
            {
                title: '点赞数',
                dataIndex: 'voteCount'
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
        const handleQuery = (params: any) => {
            loading.value = true;
            axios.get("/ebook/list", {
                params: {
                    page: params.page,
                    size: params.size
                }
            }).then((response) => {
                loading.value = false;
                const data = response.data;
                if(data.success){
                    ebooks.value = data.content.list;
                    // 重置分页按钮
                    pagination.value.current = params.page;//当前页
                    pagination.value.total = data.content.total;//总条数
                } else {
                    message.error(data.message);
                }
            });
        };//onMounted 会在页面渲染之后执行, 点击页码也会触发

        /**
         * 表格点击页码时触发
         */
        const handleTableChange = (pagination: any) => {
            handleQuery({
                page: pagination.current,
                size: pagination.pageSize
            });//pagination 可以是任意对象
        };//表格点击页码时触发

        // -------- 表单 ---------
        const categoryIds = ref();
        const ebook = ref({
            cover: null,
            name: null,
            category1Id: null,
            category2Id: null,
            desc: null,
            docCount: 0,
            viewCount: 0,
            voteCount: 0
        });
        const modalVisible = ref(false);
        const modalLoading = ref(false);
        const handleModalOk = () => {
            modalLoading.value = true;
            ebook.value.category1Id = categoryIds.value[0];
            ebook.value.category2Id = categoryIds.value[1];
            axios.post("/ebook/save", ebook.value).then((response) => {
                modalLoading.value = false;
                const data = response.data; // data = commonResp
                if (data.success) {
                    modalVisible.value = false;

                    // 重新加载列表
                    handleQuery({
                        page: pagination.value.current,
                        size: pagination.value.pageSize,
                    });
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
            ebook.value = Object.assign({}, record);
            categoryIds.value = [ebook.value.category1Id, ebook.value.category2Id]
        };

        const add = () => {
            ebook.value = {
                    cover: null,
                    name: null,
                    category1Id: null,
                    category2Id: null,
                    desc: null,
                    docCount: 0,
                    viewCount: 0,
                    voteCount: 0
            };
            modalVisible.value = true;
        }

        /* search categories*/
        const level1 =  ref();
        /**
         * 查询所有分类
         **/
        const handleQueryCategory = () => {
            loading.value = true;
            axios.get("/category/all").then((response) => {
                loading.value = false;
                const data = response.data;
                if (data.success) {
                    const categorys = data.content;
                    console.log("原始数组：", categorys);

                    level1.value = [];
                    level1.value = array2Tree(categorys, 0);

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
        };
        const handleDelete = (record: any) => {
            axios.delete("/ebook/" + record.id).then((response) => {
                const data = response.data;
                if (data.success) {
                    // 重新加载列表
                    handleQuery({
                        page: pagination.value.current,
                        size: pagination.value.pageSize,
                    });
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
            handleQueryCategory();
            handleQuery({
                page: 1,
                size: pagination.value.pageSize,
            });
        });

        //search
        const searchKey = ref("");
        const search = () => {
            axios.get("/ebook/list", {
                params: {
                    name: searchKey.value,
                    page: 1,
                    size: 4
                }
            }).then((response) => {
                const data = response.data;
                if (data.success) {
                    ebooks.value = data.content.list;
                } else {
                    message.error(data.message);
                }

            })
        }

        return {
            ebooks,
            pagination,
            columns,
            loading,
            handleTableChange,

            edit,
            add,
            handleDelete,

            ebook,
            modalVisible,
            modalLoading,
            handleModalOk,
            categoryIds,
            level1,

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
