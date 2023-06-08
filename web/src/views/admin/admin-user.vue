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
                :data-source="users"
                v-model:pagination="pagination"
                :loading="loading"
                @change="handleTableChange"
            >   <!-- record is an item of data source, @change pass a new pagination object to handleTableChange-->

                <template #action="{ record }">
                    <a-space size="small">
                        <a-button type="primary" @click="resetPass(record)">
                            Reset Password
                        </a-button>
                        <a-button type="primary" @click="edit(record)">
                            Update Username
                        </a-button>
                        <a-popconfirm
                            title="Are you sure deleting this user?"
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
        </a-layout-content>
    </a-layout>

    <a-modal
        title="电子书表单"
        v-model:visible="modalVisible"
        :confirm-loading="modalLoading"
        @ok="handleModalOk"
    >
        <a-form :model="user" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
            <a-form-item label="username">
                <a-input v-model:value="user.name" />
            </a-form-item>
            <a-form-item label="id" >
                <a-input v-model:value="user.loginName" type="textarea" :disabled="!!user.id"/>
            </a-form-item>
            <a-form-item label="password" v-show="!user.id">
                <a-input v-model:value="user.password" type="textarea" />
            </a-form-item>
        </a-form>
    </a-modal>

    <a-modal
        title="Password Reset"
        v-model:visible="resetVisible"
        :confirm-loading="resetLoading"
        @ok="handleResetOk"
    >
        <a-form :model="user" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
            <a-form-item label="username">
                <a-input v-model:value="user.name" :disabled="true"/>
            </a-form-item>
            <a-form-item label="id" >
                <a-input v-model:value="user.loginName" type="textarea" :disabled="!!user.id"/>
            </a-form-item>
            <a-form-item label="new password">
                <a-input v-model:value="user.password" type="textarea" />
            </a-form-item>
        </a-form>
    </a-modal>
</template>

<script lang="ts">
import { defineComponent, onMounted, ref } from 'vue';
import axios from 'axios';
import {message} from "ant-design-vue";
declare let hexMd5: any;
export default defineComponent({
    name: 'AdminUser',
    setup() {
        const users = ref();
        const pagination = ref({
            current: 1,
            pageSize: 10,
            total: 0
        });
        const loading = ref(false);

        const columns = [
            {
                title: 'login name',
                dataIndex: 'loginName',
            },
            {
                title: 'username',
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
        const handleQuery = (params: any) => {
            loading.value = true;
            axios.get("/user/list", {
                params: {
                    page: params.page,
                    size: params.size
                }
            }).then((response) => {
                loading.value = false;
                const data = response.data;
                if(data.success){
                    users.value = data.content.list;
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
        const user = ref({
            id: null,
            loginName: null,
            name: null,
            password: null
        });
        const modalVisible = ref(false);
        const modalLoading = ref(false);
        const handleModalOk = () => {
            modalLoading.value = true;
            user.value.password = hexMd5(user.value.password + "3kx93");
            axios.post("/user/save", user.value).then((response) => {
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
         * reset password
         */
        const resetVisible = ref(false);
        const resetLoading = ref(false);
        const resetPass = (record: any) => {
            resetVisible.value = true;
            user.value = Object.assign({}, record);
            user.value.password = null;
        };

        const handleResetOk = () => {
            resetLoading.value = true;
            user.value.password = hexMd5(user.value.password + "3kx93");
            axios.post("/user/save", user.value).then((response) => {
                resetLoading.value = false;
                const data = response.data; // data = commonResp
                if (data.success) {
                    resetVisible.value = false;
                    // 重新加载列表
                    handleQuery({
                        page: pagination.value.current,
                        size: pagination.value.pageSize,
                    });
                } else {
                    message.error(data.message);
                }
            });
        };



        /**
         * 编辑
         */
        const edit = (record: any) => {
            modalVisible.value = true;
            user.value = Object.assign({}, record);
        };

        const add = () => {
            user.value = {
                id: null,
                loginName: null,
                name: null,
                password: null
            };
            modalVisible.value = true;
        }

        /* search categories*/
        const level1 =  ref();
        let categories : any = [];

        const handleDelete = (id: number) => {
            axios.delete("/user/" + id).then((response) => {
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
            handleQuery({
                page: pagination.value.current,
                size: pagination.value.pageSize,
            });
        });

        //search
        const searchKey = ref("");
        const search = () => {
            axios.get("/user/list", {
                params: {
                    name: searchKey.value,
                    page: 1,
                    size: 10
                }
            }).then((response) => {
                const data = response.data;
                if (data.success) {
                    users.value = data.content.list;
                } else {
                    message.error(data.message);
                }

            })
        }

        return {
            users,
            pagination,
            columns,
            loading,
            handleTableChange,

            resetPass,
            resetVisible,
            resetLoading,
            handleResetOk,

            edit,
            add,
            handleDelete,

            user,
            modalVisible,
            modalLoading,
            handleModalOk,
            categoryIds,
            level1,
            categories,


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
