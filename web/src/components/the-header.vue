<template>
    <a-layout-header class="header">
            <div class="logo" />
            <a class="login-menu" v-if="user.id">
                <span>Hello, {{user.name}}</span>
            </a>
            <a class="login-menu" @click="showLoginModal" v-if="!user.id">
              <span>Login</span>
            </a>
            <a-popconfirm
                    title="Log out?"
                    ok-text="Yes"
                    cancel-text="No"
                    @confirm="logout()"
            >
              <a class="login-menu" v-show="user.id">
                  <span>Logout</span>
              </a>
            </a-popconfirm>
            <a-menu
                    theme="dark"
                    mode="horizontal"
                    :style="{ lineHeight: '64px' }"
            >
                <a-menu-item key="/">
                    <router-link to="/">homepage</router-link>
                </a-menu-item>
                <a-menu-item key="/admin/ebook" v-if="user.id">
                    <router-link to="/admin/ebook">admin</router-link>
                </a-menu-item>
                <a-menu-item key="/admin/user" v-if="user.id">
                    <router-link to="/admin/user">users</router-link>
                </a-menu-item>
                <a-menu-item key="/about">
                    <router-link to="/about">about</router-link>
                </a-menu-item>
                <a-menu-item key="/admin/category" v-if="user.id">
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
                <a-input v-model:value="user.password" type="password"  />
            </a-form-item>
        </a-form>
    </a-modal>
</template>

<script lang="ts">
  import {defineComponent, ref, computed} from 'vue';
  import axios from "axios";
  import {message} from "ant-design-vue";
  import store from "@/store";
  declare let hexMd5: any;
  export default defineComponent({
      name: 'the-header',
      setup() {
          //modal
          const visible = ref(false);
          const user = computed(() => store.state.user);
          const loading = ref(false);
          const handleOk = () => {
            loading.value = true;
            let u = Object.assign({}, user.value);
            u.password = hexMd5(u.password + "3kx93");
            axios.post('/user/login', u).then((response) => {
                const res = response.data;
                if (res.success) {
                    loading.value = false;
                    message.success('登录成功');
                    store.commit('setUser', res.content);
                    visible.value = false;
                } else {
                    loading.value = false;
                    message.error(res.message);
                }
            })
          };

          const logout = () => {
              axios.get('/user/logout/' + user.value.token).then((response) => {
                  const res = response.data;
                  if (res.success) {
                      store.commit('setUser', {});
                      message.success('Successfully logged out');
                  } else {
                      message.error(res.message);
                  }
              })
          };


          const showLoginModal = () => {
              user.value.loginName = '';
              user.value.password = '';
              visible.value = true;
          };

          return {
              showLoginModal,
              visible,
              handleOk,
              user,
              loading,
              logout

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


