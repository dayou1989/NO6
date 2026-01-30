<template>
	<div class="detail-page" :style='{}'>
        <div class="breadcrumb-wrapper" style="width: 100%;">
            <div class="bread_view">
                <el-breadcrumb separator=">" class="breadcrumb">
                    <el-breadcrumb-item class="first_breadcrumb" :to="{ path: '/' }">首页</el-breadcrumb-item>
                    <el-breadcrumb-item class="second_breadcrumb" v-for="(item,index) in breadList" :key="index">{{item.name}}</el-breadcrumb-item>
                </el-breadcrumb>
            </div>
            <div class="back_view">
                <el-button class="back_btn" @click="backClick" type="primary">返回</el-button>
            </div>
        </div>
		<div class="detail_view">
			<div class="info_view">
				<div class="title_view">
					<div class="detail_title">
						通知记录详情
					</div>
				</div>
				<el-descriptions :column="2" border>
					<el-descriptions-item label="ID">{{ detail.id }}</el-descriptions-item>
					<el-descriptions-item label="通知ID">{{ detail.tongzhiId }}</el-descriptions-item>
					<el-descriptions-item label="接收人">{{ detail.receiver }}</el-descriptions-item>
					<el-descriptions-item label="发送渠道">
						<el-tag v-if="detail.channelType === 'SMS'" type="success">短信</el-tag>
						<el-tag v-else-if="detail.channelType === 'EMAIL'" type="primary">邮件</el-tag>
						<el-tag v-else-if="detail.channelType === 'SYSTEM'" type="info">系统消息</el-tag>
					</el-descriptions-item>
					<el-descriptions-item label="发送状态" :span="2">
						<el-tag v-if="detail.status === 'PENDING'" type="warning">待发送</el-tag>
						<el-tag v-else-if="detail.status === 'SUCCESS'" type="success">发送成功</el-tag>
						<el-tag v-else-if="detail.status === 'FAILED'" type="danger">发送失败</el-tag>
					</el-descriptions-item>
					<el-descriptions-item label="重试次数">{{ detail.retryCount }}/{{ detail.maxRetryCount }}</el-descriptions-item>
					<el-descriptions-item label="最后重试时间">{{ detail.lastRetryTime || '-' }}</el-descriptions-item>
					<el-descriptions-item label="错误信息" :span="2">{{ detail.errorMessage || '-' }}</el-descriptions-item>
					<el-descriptions-item label="通知内容" :span="2">
						<div class="content-text">{{ detail.content }}</div>
					</el-descriptions-item>
					<el-descriptions-item label="创建时间">{{ detail.addtime }}</el-descriptions-item>
					<el-descriptions-item label="更新时间">{{ detail.updatetime }}</el-descriptions-item>
				</el-descriptions>
				<div class="btn_view">
					<el-button v-if="detail.status === 'FAILED' && detail.retryCount < detail.maxRetryCount" 
						class="retry_btn" type="warning" @click="retryClick">重试发送</el-button>
					<el-button class="del_btn" type="danger" @click="delClick">删除记录</el-button>
				</div>
			</div>
		</div>
	</div>
</template>
<script setup>
	import axios from 'axios'
	import {
		ref,
		getCurrentInstance,
		onMounted,
		nextTick,
		computed
	} from 'vue';
	import {
		ElMessageBox
	} from 'element-plus'
	import {
		useRoute,
		useRouter
	} from 'vue-router';
	import {
		useStore
	} from 'vuex';
	const store = useStore()
	const user = computed(()=>store.getters['user/session'])
	const context = getCurrentInstance()?.appContext.config.globalProperties;
	const route = useRoute()
	const router = useRouter()
	const tableName = 'tongzhirecord'
	const formName = '通知记录'
	const breadList = ref([{
		name: formName
	}])
	const backClick = () =>{
		history.back()
	}
	const detail = ref({})
	const getDetail = () => {
		context?.$http({
			url: `${tableName}/detail/${route.query.id}`,
			method: 'get'
		}).then(res => {
			detail.value = res.data.data
		})
	}
	const retryClick = () => {
		ElMessageBox.confirm('是否重试发送该通知？', '提示', {
			confirmButtonText: '是',
			cancelButtonText: '否',
			type: 'warning',
		}).then(()=>{
			context?.$http({
				url: `${tableName}/retry/${detail.value.id}`,
				method: 'post'
			}).then(res=>{
				context?.$toolUtil.message('重试成功，通知已加入发送队列','success',()=>{
					getDetail()
				})
			}).catch(err => {
				context?.$toolUtil.message(err.response.data.msg || '重试失败', 'error')
			})
		}).catch(_ => {})
	}
	const delClick = () => {
		ElMessageBox.confirm(`是否删除该${formName}记录？`, '提示', {
			confirmButtonText: '是',
			cancelButtonText: '否',
			type: 'warning',
		}).then(()=>{
			context?.$http({
				url: `${tableName}/delete`,
				method: 'post',
				data: [detail.value.id]
			}).then(res=>{
				context?.$toolUtil.message('删除成功','success',()=>{
					history.back()
				})
			})
		}).catch(_ => {})
	}
	const init = () => {
		getDetail()
	}
	onMounted(()=>{
		init()
	})
</script>
<style lang="scss" scoped>
	.back_view {
		border-radius: 4px;
		padding: 10px 0px;
		margin: 10px auto;
		background: none;
		width: 100%;
		text-align: right;
		.back_btn {
			border: 1px solid var(--theme);
			cursor: pointer;
			border-radius: 0px;
			padding: 0 24px;
			color: #fff;
			background: var(--theme);
			width: auto;
			font-size: 14px;
			height: 34px;
		}
		.back_btn:hover {
		}
	}
	.bread_view {
		:deep(.breadcrumb) {
			.el-breadcrumb__separator {
			}
			.first_breadcrumb {
				.el-breadcrumb__inner {
				}
			}
			.second_breadcrumb {
				.el-breadcrumb__inner {
				}
			}
		}
	}
	.detail_view{
		.info_view {
			.title_view {
				.detail_title {
					font-size: 18px;
					font-weight: bold;
					margin-bottom: 20px;
				}
			}
			.content-text {
				word-break: break-all;
				white-space: pre-wrap;
				line-height: 1.6;
			}
			.btn_view {
				margin-top: 20px;
				.retry_btn {
				}
				.retry_btn:hover {
				}
				.del_btn {
				}
				.del_btn:hover {
				}
			}
		}
	}
</style>
