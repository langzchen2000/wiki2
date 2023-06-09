<a-layout-footer style="text-align: center">
 blogger: {{user.name}}
</a-layout-footer>
<script lang="ts">
import {computed, defineComponent} from 'vue';
import store from "@/store";

export default defineComponent({
    name: 'the-footer',
    setup() {
        const user = computed(() => store.state.user);

        return {
            user
        }
    }
})
</script>