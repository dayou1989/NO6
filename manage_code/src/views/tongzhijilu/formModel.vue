<template>
	<div>
		<el-dialog modal-class="edit_form_modal" class="edit_form" v-model="formVisible" :title="formTitle" width="60%" destroy-on-close :fullscreen='false'>
			<el-form class="formModel_form" ref="formRef" :model="form" :rules="rules">
				<el-row >
					<el-col :span="12">
						<el-form-item label="通知编号" prop="tongzhibianhao">
							<el-input class="list_inp" v-model="form.tongzhibianhao" :readonly="true" placeholder="通知编号" />
						</el-form-item>
					</el-col>
					<el-col :span="12">
						<el-form-item label="预约编号" prop="yuyuebianhao">
							<el-input class="list_inp" v-model="form.yuyuebianhao" :readonly="true" placeholder="预约编号" />
						</el-form-item>
					</el-col>
					<el-col :span="12">
						<el-form-item label="用户账号" prop="zhanghao">
							<el-input class="list_inp" v-model="form.zhanghao" :readonly="true" placeholder="用户账号" />
						</el-form-item>
					</el-col>
					<el-col :span="12">
						<el-form-item label="用户手机" prop="shouji">
							<el-input class="list_inp" v-model="form.shouji" :readonly="true" placeholder="用户手机" />
						</el-form-item>
					</el-col>
					<el-col :span="12">
						<el-form-item label="医生账号" prop="yishengzhanghao">
							<el-input class="list_inp" v-model="form.yishengzhanghao" :readonly="true" placeholder="医生账号" />
						</el-form-item>
					</el-col>
					<el-col :span="12">
						<el-form-item label="医生电话" prop="dianhua">
							<el-input class="list_inp" v-model="form.dianhua" :readonly="true" placeholder="医生电话" />
						</el-form-item>
					</el-col>
					<el-col :span="12">
						<el-form-item label="就诊时间" prop="jiuzhenshijian">
							<el-input class="list_inp" v-model="form.jiuzhenshijian" :readonly="true" placeholder="就诊时间" />
						</el-form-item>
					</el-col>
					<el-col :span="12">
						<el-form-item label="发送渠道" prop="fasongqudao">
							<el-tag v-if="form.fasongqudao=='sms'" type="success">短信</el-tag>
							<el-tag v-else-if="form.fasongqudao=='站内信'" type="primary">站内信</el-tag>
							<el-tag v-else>{{form.fasongqudao}}</el-tag>
						</el-form-item>
					</el-col>
					<el-col :span="12">
						<el-form-item label="发送状态" prop="fasongzhuangtai">
							<el-tag v-if="form.fasongzhuangtai=='成功'" type="success">成功</el-tag>
							<el-tag v-else-if="form.fasongzhuangtai=='失败'" type="danger">失败</el-tag>
							<el-tag v-else type="warning">待发送</el-tag>
						</el-form-item>
					</el-col>
					<el-col :span="12">
						<el-form-item label="重试次数" prop="chongshicishu">
							<el-input class="list_inp" :value="form.chongshicishu + '/' + form.zuidachongshi" :readonly="true" placeholder="重试次数" />
						</el-form-item>
					</el-col>
					<el-col :span="12">
						<el-form-item label="发送时间" prop="fasongshijian">
							<el-input class="list_inp" v-model="form.fasongshijian" :readonly="true" placeholder="发送时间" />
						</el-form-item>
					</el-col>
					<el-col :span="12">
						<el-form-item label="下次重试时间" prop="xiacichongshishijian">
							<el-input class="list_inp" v-model="form.xiacichongshishijian" :readonly="true" placeholder="下次重试时间" />
						</el-form-item>
					</el-col>
					<el-col :span="24">
						<el-form-item label="通知内容" prop="tongzhineirong">
							<el-input class="list_textarea" v-model="form.tongzhineirong" type="textarea" :rows="3" :readonly="true" placeholder="通知内容" />
						</el-form-item>
					</el-col>
					<el-col :span="24" v-if="form.shibaoyuanyin">
						<el-form-item label="失败原因" prop="shibaoyuanyin">
							<el-input class="list_textarea" v-model="form.shibaoyuanyin" type="textarea" :rows="2" :readonly="true" placeholder="失败原因" />
						</el-form-item>
					</el-col>
					<el-col :span="24" v-if="form.beizhu">
						<el-form-item label="备注" prop="beizhu">
							<el-input class="list_inp" v-model="form.beizhu" :readonly="true" placeholder="备注" />
						</el-form-item>
					</el-col>
				</el-row>
			</el-form>
			<template #footer>
				<span class="formModel_btn_box">
					<el-button class="cancel_btn" @click="closeClick">关闭</el-button>
					<el-button class="confirm_btn" type="warning" @click="retryClick" v-if="form.fasongzhuangtai!='成功' && form.chongshicishu < form.zuidachongshi">
						重试发送
					</el-button>
				</span>
			</template>
		</el-dialog>
	</div>
</template>
<script setup>
	import {
		reactive,
		ref,
		getCurrentInstance,
		nextTick,
		computed,
		defineEmits
	} from 'vue'
    import {
        useStore
    } from 'vuex';
    const store = useStore()
    const user = computed(()=>store.getters['user/session'])
	const context = getCurrentInstance()?.appContext.config.globalProperties;	
	const emit = defineEmits(['formModelChange'])
	const tableName = 'tongzhijilu'
	const formName = '通知发送记录'
	const form = ref({})
	const disabledForm = ref({
        tongzhibianhao : false,
        yuyuebianhao : false,
        yishengzhanghao : false,
        dianhua : false,
        zhanghao : false,
        shouji : false,
        jiuzhenshijian : false,
        tongzhineirong : false,
        fasongqudao : false,
        fasongzhuangtai : false,
        shibaoyuanyin : false,
        chongshicishu : false,
        zuidachongshi : false,
        xiacichongshishijian : false,
        fasongshijian : false,
        beizhu : false,
	})
	const formVisible = ref(false)
	const isAdd = ref(false)
	const formTitle = ref('')
    
	const rules = ref({
	})
	const formRef = ref(null)
	const id = ref(0)
	const type = ref('')

	const getUUID =()=> {
      return new Date().getTime();
    }
	const resetForm = () => {
		form.value = {
			tongzhibianhao: '',
			yuyuebianhao: '',
			yishengzhanghao: '',
			dianhua: '',
			zhanghao: '',
			shouji: '',
			jiuzhenshijian: '',
			tongzhineirong: '',
			fasongqudao: '',
			fasongzhuangtai: '',
			shibaoyuanyin: '',
			chongshicishu: 0,
			zuidachongshi: 3,
			xiacichongshishijian: '',
			fasongshijian: '',
			beizhu: '',
		}
	}
	const getInfo = ()=>{
		context?.$http({
			url: `${tableName}/info/${id.value}`,
			method: 'get'
		}).then(res => {
			form.value = res.data.data
			formVisible.value = true
		})
	}
	const init=(formId=null,formType='add',formNames='',row=null,table=null,statusColumnName=null,tips=null,statusColumnValue=null)=>{
		resetForm()
		if(formId){
			id.value = formId
			type.value = formType
		}
		if(formType == 'info'){
			isAdd.value = false
			formTitle.value = '查看' + formName
			getInfo()
		}
	}
	defineExpose({
		init
	})
	const closeClick = () => {
		formVisible.value = false
	}
	const retryClick = () => {
		context?.$http({
			url: `${tableName}/retry/${id.value}`,
			method: 'post'
		}).then(res => {
			if(res.data.code==0){
				context?.$toolUtil.message(res.data.msg || '已加入重试队列', 'success',()=>{
					emit('formModelChange')
					formVisible.value = false
				})
			}else{
				context?.$toolUtil.message(res.data.msg, 'error')
			}
		})
	}
</script>
<style lang="scss" scoped>
	.formModel_form{
		:deep(.el-form-item) {
			.el-form-item__label {
			}
			.el-form-item__content {
				.list_inp {
				}
				.list_textarea {
				}
			}
		}
	}
	.formModel_btn_box {
		.cancel_btn {
		}
		.cancel_btn:hover {
		}
		.confirm_btn {
		}
		.confirm_btn:hover {
		}
	}
</style>
