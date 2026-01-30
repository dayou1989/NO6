<template>
	<div class="list-page" :style='{}'>
        <div class="breadcrumb-wrapper" style="width: 100%;">
            <div class="bread_view">
                <el-breadcrumb separator=">" class="breadcrumb">
                    <el-breadcrumb-item class="first_breadcrumb" :to="{ path: '/' }">首页</el-breadcrumb-item>
                    <el-breadcrumb-item class="second_breadcrumb" v-for="(item,index) in breadList" :key="index">{{item.name}}</el-breadcrumb-item>
                </el-breadcrumb>
            </div>
        </div>
		<el-form :inline="true" :model="searchQuery" class="list_search">
			<div class="search_view">
				<div class="search_label">
					接收人：
				</div>
				<div class="search_box">
					<el-input class="search_inp" v-model="searchQuery.receiver" placeholder="接收人"
						clearable>
					</el-input>
				</div>
			</div>
			<div class="search_view">
				<div class="search_label">
					发送渠道：
				</div>
				<div class="search_box">
					<el-select v-model="searchQuery.channelType" placeholder="请选择渠道" clearable>
						<el-option label="全部" value=""></el-option>
						<el-option label="短信" value="SMS"></el-option>
						<el-option label="邮件" value="EMAIL"></el-option>
						<el-option label="系统消息" value="SYSTEM"></el-option>
					</el-select>
				</div>
			</div>
			<div class="search_view">
				<div class="search_label">
					发送状态：
				</div>
				<div class="search_box">
					<el-select v-model="searchQuery.status" placeholder="请选择状态" clearable>
						<el-option label="全部" value=""></el-option>
						<el-option label="待发送" value="PENDING"></el-option>
						<el-option label="发送成功" value="SUCCESS"></el-option>
						<el-option label="发送失败" value="FAILED"></el-option>
					</el-select>
				</div>
			</div>
			<div class="search_btn_view">
				<el-button class="search_btn" type="primary" @click="searchClick">搜索</el-button>
				<el-button class="add_btn" type="success" @click="viewStatistics">统计</el-button>
			</div>
		</el-form>
		<div class="page_list">
			<div class="table_box">
				<el-table :data="list" style="width: 100%">
					<el-table-column prop="id" label="ID" width="80"></el-table-column>
					<el-table-column prop="tongzhiId" label="通知ID" width="100"></el-table-column>
					<el-table-column prop="receiver" label="接收人" width="120"></el-table-column>
					<el-table-column prop="channelType" label="发送渠道" width="100">
						<template #default="scope">
							<el-tag v-if="scope.row.channelType === 'SMS'" type="success">短信</el-tag>
							<el-tag v-else-if="scope.row.channelType === 'EMAIL'" type="primary">邮件</el-tag>
							<el-tag v-else-if="scope.row.channelType === 'SYSTEM'" type="info">系统消息</el-tag>
						</template>
					</el-table-column>
					<el-table-column prop="status" label="发送状态" width="100">
						<template #default="scope">
							<el-tag v-if="scope.row.status === 'PENDING'" type="warning">待发送</el-tag>
							<el-tag v-else-if="scope.row.status === 'SUCCESS'" type="success">发送成功</el-tag>
							<el-tag v-else-if="scope.row.status === 'FAILED'" type="danger">发送失败</el-tag>
						</template>
					</el-table-column>
					<el-table-column prop="retryCount" label="重试次数" width="100">
						<template #default="scope">
							{{ scope.row.retryCount }}/{{ scope.row.maxRetryCount }}
						</template>
					</el-table-column>
					<el-table-column prop="errorMessage" label="错误信息" min-width="150" show-overflow-tooltip></el-table-column>
					<el-table-column prop="addtime" label="创建时间" width="160"></el-table-column>
					<el-table-column label="操作" width="200" fixed="right">
						<template #default="scope">
							<el-button type="primary" size="small" @click="detailClick(scope.row.id)">详情</el-button>
							<el-button v-if="scope.row.status === 'FAILED' && scope.row.retryCount < scope.row.maxRetryCount" 
								type="warning" size="small" @click="retryClick(scope.row.id)">重试</el-button>
						</template>
					</el-table-column>
				</el-table>
				<el-pagination
					background 
					:layout="layouts.join(',')"
					:total="total" 
					:page-size="listQuery.limit"
                    v-model:current-page="listQuery.page"
					prev-text="上一页"
					next-text="下一页"
					:hide-on-single-page="false"
					:style='{}'
					@size-change="sizeChange"
					@current-change="currentChange"/>
			</div>
		</div>
		<el-dialog v-model="statisticsVisible" title="通知统计" width="50%">
			<el-descriptions :column="2" border>
				<el-descriptions-item label="总记录数">{{ statisticsData.totalCount }}</el-descriptions-item>
				<el-descriptions-item label="发送成功">
					<el-tag type="success">{{ statisticsData.successCount }}</el-tag>
				</el-descriptions-item>
				<el-descriptions-item label="发送失败">
					<el-tag type="danger">{{ statisticsData.failedCount }}</el-tag>
				</el-descriptions-item>
				<el-descriptions-item label="待发送">
					<el-tag type="warning">{{ statisticsData.pendingCount }}</el-tag>
				</el-descriptions-item>
				<el-descriptions-item label="短信数量">
					<el-tag type="success">{{ statisticsData.smsCount }}</el-tag>
				</el-descriptions-item>
				<el-descriptions-item label="邮件数量">
					<el-tag type="primary">{{ statisticsData.emailCount }}</el-tag>
				</el-descriptions-item>
			</el-descriptions>
		</el-dialog>
	</div>
</template>

<script setup>
	import {
		ref,
		getCurrentInstance,
		nextTick,
        computed,
	} from 'vue';
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
	const router = useRouter()
	const route = useRoute()
	const tableName = 'tongzhirecord'
	const formName = '通知记录'
	const breadList = ref([{
		name: formName
	}])
	const list = ref([])
	const listQuery = ref({
		page: 1,
		limit: Number(10)
	})
	const total = ref(0)
	const listLoading = ref(false)
	const searchQuery = ref({
		receiver: '',
		channelType: '',
		status: ''
	})
	const layouts = ref(["total","prev","pager","next","sizes","jumper"])
	const statisticsVisible = ref(false)
	const statisticsData = ref({
		totalCount: 0,
		successCount: 0,
		failedCount: 0,
		pendingCount: 0,
		smsCount: 0,
		emailCount: 0
	})
	const searchClick = () => {
		listQuery.value.page = 1
		getList()
	}
	const sizeChange = (size) => {
		listQuery.value.limit = size
		getList()
	}
	const currentChange = (page) => {
		listQuery.value.page = page
		getList()
	}
	const getList = () => {
		listLoading.value = true
		let params = JSON.parse(JSON.stringify(listQuery.value))
		if(searchQuery.value.receiver && searchQuery.value.receiver != ''){
			params.receiver = '%' + searchQuery.value.receiver + '%'
		}
		if(searchQuery.value.channelType && searchQuery.value.channelType != ''){
			params.channelType = searchQuery.value.channelType
		}
		if(searchQuery.value.status && searchQuery.value.status != ''){
			params.status = searchQuery.value.status
		}
		context?.$http({
			url: `${tableName}/page`,
			method: 'get',
			params: params
		}).then(res => {
			listLoading.value = false
			list.value = res.data.data.list
			total.value = Number(res.data.data.total)
		})
	}
	const detailClick = (id) => {
		router.push(`${tableName}Detail?id=` + id)
	}
	const retryClick = (id) => {
		context?.$http({
			url: `${tableName}/retry/${id}`,
			method: 'post'
		}).then(res => {
			context?.$toolUtil.message('重试成功，通知已加入发送队列', 'success')
			getList()
		}).catch(err => {
			context?.$toolUtil.message(err.response.data.msg || '重试失败', 'error')
		})
	}
	const viewStatistics = () => {
		context?.$http({
			url: `${tableName}/statistics`,
			method: 'get'
		}).then(res => {
			statisticsData.value = res.data.data
			statisticsVisible.value = true
		})
	}
	const init = async() => {
        if(context.$toolUtil.storageGet('frontToken') && !user.value.id){
            await store.dispatch("user/getSession")
        }
		getList()
	}
	init()
</script>
<style lang="scss" scoped>
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
	.list_search {
		.search_view {
			.search_label {
			}
			.search_box {
				:deep(.search_inp) {
					.is-focus {
						box-shadow: none !important;
					}
				}
			}
		}
		.search_btn_view {
			.search_btn {
			}
			.search_btn:hover {
			}
			.add_btn {
			}
			.add_btn:hover {
			}
		}
	}
	.page_list {
		.table_box {
			margin-top: 20px;
		}
	}
	.el-pagination {
		margin-top: 20px;
		:deep(.el-pagination__total) {
		}
		:deep(.btn-prev) {
		}
		:deep(.btn-next) {
		}
		:deep(.btn-prev:disabled) {
		}
		:deep(.btn-next:disabled) {
		}
		:deep(.el-pager) {
			.number {
			}
			.number:hover {
			}
			.number.is-active {
			}
		}
		:deep(.el-pagination__sizes) {
			.el-select {
				.select-trigger{
					height: 100%;
					.el-input{
						height: 100%;
						.is-focus {
							box-shadow: none !important;
						}
					}
				}
			}
		}
		:deep(.el-pagination__jump) {
			.el-input {
				.is-focus {
					box-shadow: none !important;
				}
			}
		}
	}
</style>
