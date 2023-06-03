<template>
    <a-layout>
        <a-layout-sider width="200" style="background: #fff">
            <a-menu
                    mode="inline"
                    :style="{ height: '100%', borderRight: 0 }"
                    @click="handleMenuClick"
            >
                <a-menu-item key="welcome">
                    <MailOutlined />
                    <span>欢迎</span>
                </a-menu-item>
                <a-sub-menu v-for="item in level1" :key="item.id">
                    <template #title>
                        {{item.name}}
                    </template>
                    <a-menu-item v-for="item1 in item.children" :key="item1.id">
                        {{ item1.name }}
                    </a-menu-item>
                </a-sub-menu>
            </a-menu>
        </a-layout-sider>
        <a-layout-content
                :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
        >
            <div class="welcome" v-show="isShowWelcome">
                <h1>Welcome to Wiki2 project</h1>
            </div>
            <a-list v-show="!isShowWelcome"   item-layout="vertical" size="large" :grid="{ gutter: 20, column: 3 }" :data-source="ebooks">
                <template #renderItem="{ item }">
                    <a-list-item key="item.name">
                        <template #actions>
                          <span v-for="{ type, text } in actions" :key="type">
                            <component v-bind:is="type" style="margin-right: 8px" />
                            {{ text }}
                          </span>
                        </template>
                        <a-list-item-meta :description="item.description">
                            <template #title>
                                <a :href="item.href">{{ item.name }}</a>
                            </template>
                            <template #avatar><a-avatar :src="item.cover"/></template>
                        </a-list-item-meta>
                    </a-list-item>
                </template>
            </a-list>
        </a-layout-content>
    </a-layout>
</template>

<script lang="ts">
import { defineComponent, onMounted, ref, reactive, toRef } from 'vue';
import axios from 'axios';
import {message} from "ant-design-vue";

export default defineComponent({
    name: 'HomeView',
    setup() {
        const ebooks = ref();


        onMounted(() => {
            handleQueryCategory();

        });
        /*sidebar*/
        const level1 =  ref();
        let categories : any = [];
        /**
         * 查询所有分类
         **/
        const handleQueryCategory = () => {
            axios.get("/category/all").then((response) => {

                const data = response.data;
                if (data.success) {
                    categories = data.content;
                    console.log("原始数组：", categories);

                    level1.value = [];
                    level1.value = array2Tree(categories, 0);
                    axios.get("/ebook/list", {
                        params: {
                            page: 1,
                            size: 100
                        }
                    }).then((response) => {
                        const data = response.data;
                        ebooks.value = data.content.list;
                    });

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

        /*welcome*/
        const isShowWelcome = ref(true);
        let categoryId2 = 0;
        const handleMenuClick = (value: any)  => {
            if (value.key === "welcome") {
                isShowWelcome.value = true;
            } else {
                isShowWelcome.value = false;
                categoryId2 = value.key;
                handleQueryEbook();
            }
        }

        const handleQueryEbook = () => {
            axios.get("/ebook/list", {
                params: {
                    page: 1,
                    size: 100,
                    categoryId2: categoryId2
                }
            }).then((response) => {
                const data = response.data;
                ebooks.value = data.content.list;
            })
        }
        return {
            ebooks,
            // ebooks2: toRef(ebooks1, "books"),
            // listData,
            pagination: {
                onChange: (page: any) => {
                    console.log(page);
                },
                pageSize: 3,
            },
            actions: [
                { type: 'StarOutlined', text: '156' },
                { type: 'LikeOutlined', text: '156' },
                { type: 'MessageOutlined', text: '2' },
            ],
            level1,
            isShowWelcome,
            handleMenuClick
        }
    }
});
</script>

<style scoped>
.ant-avatar {
    width: 50px;
    height: 50px;
    line-height: 50px;
    border-radius: 8%;
    margin: 5px 0;
}
</style>
