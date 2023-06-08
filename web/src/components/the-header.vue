<template>
    <a-layout-header class="header">
            <div class="logo" />
            <a class="login-menu" @click="showLoginModal">
              <span>登录</span>
            </a>
            <a-menu
                    theme="dark"
                    mode="horizontal"
                    :style="{ lineHeight: '64px' }"
            >
                <a-menu-item key="/">
                    <router-link to="/">homepage</router-link>
                </a-menu-item>
                <a-menu-item key="/admin/ebook">
                    <router-link to="/admin/ebook">admin</router-link>
                </a-menu-item>
                <a-menu-item key="/about">
                    <router-link to="/about">about</router-link>
                </a-menu-item>
                <a-menu-item key="/admin/category">
                    <router-link to="/admin/category">category</router-link>
                </a-menu-item>

            </a-menu>
    </a-layout-header>
    <a-modal
        v-model:visible="visible"
        title="Login"
        :confirm-loading="loading"
        @ok="handleOk"
    >
        <a-form :model="user" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
            <a-form-item label="Login Name" >
                <a-input v-model:value="user.loginName" type="textarea"/>
            </a-form-item>
            <a-form-item label="Password">
                <a-input v-model:value="user.password" type="textarea" />
            </a-form-item>
        </a-form>
    </a-modal>
</template>

<script lang="ts">
  import {defineComponent, ref} from 'vue';
  import axios from "axios";
  import {message} from "ant-design-vue";
  declare let hexMd5: any;
  export default defineComponent({
      name: 'the-header',
      setup() {
          //modal

          const visible = ref(false);
          const user = ref({
              loginName: '',
              password: ''
          })
          const loading = ref(false);
          const handleOk = () => {
            loading.value = true;
            user.value.password = hexMd5(user.value.password);
            axios.post('/user/login', user.value).then((response) => {
                const res = response.data;
                if (res.success) {
                    loading.value = false;
                    message.success('登录成功');
                    visible.value = false;
                } else {
                    loading.value = false;
                    message.error(res.message);
                }
            })

          };

          const showLoginModal = () => {
              visible.value = true;
          };




          return {
              showLoginModal,
              visible,
              handleOk,
              user,
              loading

          }
      }
  })
</script>

<style>
  .login-menu {
      float: right;
      color: #fff;
  }
</style>


