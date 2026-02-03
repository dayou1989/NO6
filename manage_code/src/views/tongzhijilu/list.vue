<template>
	<div>
		<div class="center_view">
			<div class="list_search_view">
				<el-form :model="searchQuery" class="search_form" >
					<div class="search_view">
						<div class="search_label">
							预约编号：
						</div>
						<div class="search_box">
							<el-input class="search_inp" v-model="searchQuery.yuyuebianhao" placeholder="预约编号"
								clearable>
							</el-input>
						</div>
					</div>
					<div class="search_view">
						<div class="search_label">
							用户账号：
						</div>
						<div class="search_box">
							<el-input class="search_inp" v-model="searchQuery.zhanghao" placeholder="用户账号"
								clearable>
							</el-input>
						</div>
					</div>
					<div class="search_view">
						<div class="search_label">
							发送状态：
						</div>
						<div class="search_box">
							<el-select v-model="searchQuery.fasongzhuangtai" placeholder="请选择发送状态" clearable>
								<el-option label="待发送" :value="0"></el-option>
								<el-option label="发送成功" :value="1"></el-option>
								<el-option label="发送失败" :value="2"></el-option>
							</el-select>
						</div>
					</div>
					<div class="search_view">
						<div class="search_label">
							通知类型：
						</div>
						<div class="search_box">
							<el-select v-model="searchQuery.tongzhileixing" placeholder="请选择通知类型" clearable>
								<el-option label="预约成功通知" :value="1"></el-option>
								<el-option label="就诊前24小时提醒" :value="2"></el-option>
								<el-option label="就诊前1小时提醒" :value="3"></el-option>
								<el-option label="就诊当天提醒" :value="4"></el-option>
							</el-select>
						</div>
					</div>
					<div class="search_btn_view">
						<el-button class="search_btn" type="primary" @click="searchClick()" size="small">搜索</el-button>
					</div>
				</el-form>
				<div class="btn_view">
					<el-button class="add_btn" type="success" @click="statisticsClick" v-if="btnAuth('tongzhijilu','统计')">
						<i class="iconfont icon-tongji"></i>
						发送统计
					</el-button>
					<el-button class="edit_btn" type="warning" :disabled="selRows.length?false:true" @click="retryBatchClick" v-if="btnAuth('tongzhijilu','重试')">
						<i class="iconfont icon-zhongshi"></i>
						批量重试
					</el-button>
					<el-button class="del_btn" type="danger" :disabled="selRows.length?false:true" @click="delClick(null)"  v-if="btnAuth('tongzhijilu','删除')">
						<i class="iconfont icon-shanchu4"></i>
						删除
					</el-button>
				</div>
			</div>
			<el-table
				v-loading="listLoading"
				border
				:stripe='false'
				@selection-change="handleSelectionChange"
				ref="table"
				v-if="btnAuth('tongzhijilu','查看')"
				:data="list"
				@row-click="listChange">
				<el-table-column :resizable='true' align="left" header-align="left" type="selection" width="55" />
				<el-table-column label="序号" width="70" :resizable='true' align="left" header-align="left">
					<template #default="scope">{{ (listQuery.page-1)*listQuery.limit+scope.$index + 1}}</template>
				</el-table-column>
				<el-table-column min-width="140"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="yuyuebianhao"
					label="预约编号">
					<template #default="scope">
						{{scope.row.yuyuebianhao}}
					</template>
				</el-table-column>
				<el-table-column min-width="140"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="zhanghao"
					label="用户账号">
					<template #default="scope">
						{{scope.row.zhanghao}}
					</template>
				</el-table-column>
				<el-table-column min-width="140"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="xingming"
					label="用户姓名">
					<template #default="scope">
						{{scope.row.xingming}}
					</template>
				</el-table-column>
				<el-table-column min-width="140"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="shouji"
					label="手机号">
					<template #default="scope">
						{{scope.row.shouji}}
					</template>
				</el-table-column>
				<el-table-column min-width="160"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="tongzhileixingName"
					label="通知类型">
					<template #default="scope">
						<el-tag v-if="scope.row.tongzhileixing==1" type="success">{{scope.row.tongzhileixingName}}</el-tag>
						<el-tag v-else-if="scope.row.tongzhileixing==2" type="warning">{{scope.row.tongzhileixingName}}</el-tag>
						<el-tag v-else-if="scope.row.tongzhileixing==3" type="danger">{{scope.row.tongzhileixingName}}</el-tag>
						<el-tag v-else type="info">{{scope.row.tongzhileixingName}}</el-tag>
					</template>
				</el-table-column>
				<el-table-column min-width="200"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="tongzhineirong"
					label="通知内容"
					show-overflow-tooltip>
					<template #default="scope">
						{{scope.row.tongzhineirong}}
					</template>
				</el-table-column>
				<el-table-column min-width="100"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="fasongqudao"
					label="发送渠道">
					<template #default="scope">
						<el-tag v-if="scope.row.fasongqudao=='sms'" type="primary">短信</el-tag>
						<el-tag v-else-if="scope.row.fasongqudao=='app'" type="success">应用内</el-tag>
						<el-tag v-else-if="scope.row.fasongqudao=='email'" type="info">邮件</el-tag>
						<el-tag v-else>{{scope.row.fasongqudao}}</el-tag>
					</template>
				</el-table-column>
				<el-table-column min-width="100"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="fasongzhuangtaiName"
					label="发送状态">
					<template #default="scope">
						<el-tag v-if="scope.row.fasongzhuangtai==0" type="info">{{scope.row.fasongzhuangtaiName}}</el-tag>
						<el-tag v-else-if="scope.row.fasongzhuangtai==1" type="success">{{scope.row.fasongzhuangtaiName}}</el-tag>
						<el-tag v-else type="danger">{{scope.row.fasongzhuangtaiName}}</el-tag>
					</template>
				</el-table-column>
				<el-table-column min-width="100"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="chongshicishu"
					label="重试次数">
					<template #default="scope">
						{{scope.row.chongshicishu}}/{{scope.row.zuidachongshicishu}}
					</template>
				</el-table-column>
				<el-table-column min-width="160"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="jihuafasongshijian"
					label="计划发送时间">
					<template #default="scope">
						{{scope.row.jihuafasongshijian}}
					</template>
				</el-table-column>
				<el-table-column min-width="160"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="shijifasongshijian"
					label="实际发送时间">
					<template #default="scope">
						{{scope.row.shijifasongshijian}}
					</template>
				</el-table-column>
				<el-table-column min-width="200"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="shibaiyuanyin"
					label="失败原因"
					show-overflow-tooltip>
					<template #default="scope">
						<span style="color: #f56c6c;">{{scope.row.shibaiyuanyin}}</span>
					</template>
				</el-table-column>
				<el-table-column label="操作" width="200" :resizable='true' :sortable='true' align="left" header-align="left">
					<template #default="scope">
						<el-button class="view_btn" type="info" v-if=" btnAuth('tongzhijilu','查看')" @click="infoClick(scope.row.id)">
							<i class="iconfont icon-sousuo2"></i>
							查看
						</el-button>
						<el-button v-if="scope.row.fasongzhuangtai==2 && scope.row.chongshicishu < scope.row.zuidachongshicishu"
							class="edit_btn" type="warning" @click="retryClick(scope.row.id)" v-if=" btnAuth('tongzhijilu','重试')">
							<i class="iconfont icon-zhongshi"></i>
							重试
						</el-button>
						<el-button class="del_btn" type="danger" @click="delClick(scope.row.id)"  v-if="btnAuth('tongzhijilu','删除')">
							<i class="iconfont icon-shanchu4"></i>
							删除
						</el-button>
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
				:page-sizes="[10, 20, 30, 40, 50, 100]"
				@size-change="sizeChange"
				@current-change="currentChange"  />
		</div>

		<!-- 统计弹窗 -->
		<el-dialog v-model="statisticsVisible" title="通知发送统计" width="500px">
			<el-descriptions :column="1" border>
				<el-descriptions-item label="总记录数">{{statistics.totalCount}}</el-descriptions-item>
				<el-descriptions-item label="发送成功">{{statistics.successCount}}</el-descriptions-item>
				<el-descriptions-item label="发送失败">{{statistics.failCount}}</el-descriptions-item>
				<el-descriptions-item label="待发送">{{statistics.pendingCount}}</el-descriptions-item>
				<el-descriptions-item label="成功率">{{statistics.successRate}}</el-descriptions-item>
			</el-descriptions>
		</el-dialog>
	</div>
</template>

<script setup>
	import axios from 'axios'
    import moment from "moment"
	import {
		reactive,
		ref,
		getCurrentInstance,
		nextTick,
		onMounted,
		watch,
		computed,
	} from 'vue'
	import {
		useRoute,
		useRouter
	} from 'vue-router'
	import {
		ElMessageBox,
		ElMessage
	} from 'element-plus'
	import {
		useStore
	} from 'vuex';
	const store = useStore()
	const user = computed(()=>store.getters['user/session'])
	const avatar = ref(store.state.user.avatar)
	const context = getCurrentInstance()?.appContext.config.globalProperties;
	//基础信息

	const tableName = 'tongzhijilu'
	const formName = '通知记录'
	const route = useRoute()
	//基础信息
	onMounted(()=>{
	})
	//列表数据
	const list = ref(null)
	const table = ref(null)
	const listQuery = ref({
		page: 1,
		limit: 10,
		sort: 'id',
		order: 'desc'
	})
	const searchQuery = ref({})
	const selRows = ref([])
	const listLoading = ref(false)
	const listChange = (row) =>{
		nextTick(()=>{
			table.value.toggleRowSelection(row)
		})
	}
	//列表
	const getList = () => {
		listLoading.value = true
		let params = JSON.parse(JSON.stringify(listQuery.value))
		params['sort'] = 'id'
		params['order'] = 'desc'
		if(searchQuery.value.yuyuebianhao&&searchQuery.value.yuyuebianhao!=''){
			params['yuyuebianhao'] = '%' + searchQuery.value.yuyuebianhao + '%'
		}
		if(searchQuery.value.zhanghao&&searchQuery.value.zhanghao!=''){
			params['zhanghao'] = '%' + searchQuery.value.zhanghao + '%'
		}
		if(searchQuery.value.fasongzhuangtai!==undefined&&searchQuery.value.fasongzhuangtai!==''){
			params['fasongzhuangtai'] = searchQuery.value.fasongzhuangtai
		}
		if(searchQuery.value.tongzhileixing!==undefined&&searchQuery.value.tongzhileixing!==''){
			params['tongzhileixing'] = searchQuery.value.tongzhileixing
		}
		context.$http({
			url: `${tableName}/page`,
			method: 'get',
			params: params
		}).then(res => {
			listLoading.value = false
			list.value = res.data.data.list
			total.value = Number(res.data.data.total)
		})
	}
	//删
	const delClick = (id) => {
		let ids = ref([])
		if (id) {
			ids.value = [id]
		} else {
			if (selRows.value.length) {
				for (let x in selRows.value) {
					ids.value.push(selRows.value[x].id)
				}
			} else {
				return false
			}
		}
		ElMessageBox.confirm(`是否删除选中${formName}`, '提示', {
			confirmButtonText: '是',
			cancelButtonText: '否',
			type: 'warning',
		}).then(() => {
			context.$http({
				url: `${tableName}/delete`,
				method: 'post',
				data: ids.value
			}).then(res => {
				context?.$toolUtil.message('删除成功', 'success',()=>{
					getList()
				})
			})
		}).catch(_ => {})
	}
	//多选
	const handleSelectionChange = (e) => {
		selRows.value = e
	}
	//列表数据
	//分页
	const total = ref(0)
	const layouts = ref(["total","prev","pager","next","sizes","jumper"])
	const sizeChange = (size) => {
		listQuery.value.limit = size
		getList()
	}
	const currentChange = (page) => {
		listQuery.value.page = page
		getList()
	}
	//分页
	//权限验证
	const btnAuth = (e,a)=>{
		return context?.$toolUtil.isAuth(e,a)
	}
	//搜索
	const searchClick = () => {
		listQuery.value.page = 1
		getList()
	}
	//查看
	const infoClick = (id=null)=>{
		if(id){
			ElMessage.info('查看功能待实现')
		}
		else if(selRows.value.length){
			ElMessage.info('查看功能待实现')
		}
	}

	// 统计相关
	const statisticsVisible = ref(false)
	const statistics = ref({
		totalCount: 0,
		successCount: 0,
		failCount: 0,
		pendingCount: 0,
		successRate: '0.00%'
	})

	const statisticsClick = () => {
		context.$http({
			url: `${tableName}/statistics`,
			method: 'get'
		}).then(res => {
			statistics.value = res.data.data
			statisticsVisible.value = true
		})
	}

	// 重试发送
	const retryClick = (id) => {
		ElMessageBox.confirm('是否重试发送该通知？', '提示', {
			confirmButtonText: '是',
			cancelButtonText: '否',
			type: 'warning',
		}).then(() => {
			context.$http({
				url: `${tableName}/retry/${id}`,
				method: 'post'
			}).then(res => {
				ElMessage.success(res.data.msg || '重试成功')
				getList()
			}).catch(err => {
				ElMessage.error(err.response?.data?.msg || '重试失败')
			})
		}).catch(_ => {})
	}

	// 批量重试
	const retryBatchClick = () => {
		if (!selRows.value.length) {
			ElMessage.warning('请选择要重试的记录')
			return
		}
		let ids = []
		for (let x in selRows.value) {
			ids.push(selRows.value[x].id)
		}
		ElMessageBox.confirm(`是否批量重试选中的${ids.length}条通知？`, '提示', {
			confirmButtonText: '是',
			cancelButtonText: '否',
			type: 'warning',
		}).then(() => {
			context.$http({
				url: `${tableName}/retryBatch`,
				method: 'post',
				data: ids
			}).then(res => {
				ElMessage.success(res.data.msg || '批量重试完成')
				getList()
			}).catch(err => {
				ElMessage.error(err.response?.data?.msg || '批量重试失败')
			})
		}).catch(_ => {})
	}

	//初始化
	const init = () => {
		getList()
	}
	init()
</script>

<style lang="scss" scoped>
	// 操作盒子
	.list_search_view {
		// 搜索盒子
		.search_form {
			// 子盒子
			.search_view {
				// 搜索label
				.search_label {
				}
				// 搜索item
				.search_box {
					// 输入框
					:deep(.search_inp) {
					}
				}
			}
			// 搜索按钮盒子
			.search_btn_view {
				// 搜索按钮
				.search_btn {
				}
				// 搜索按钮-悬浮
				.search_btn:hover {
				}
			}
		}
		//头部按钮盒子
		.btn_view {
			// 其他
			:deep(.el-button--default){
			}
			// 其他-悬浮
			:deep(.el-button--default:hover){
			}
			// 新增
			:deep(.el-button--success){
			}
			// 新增-悬浮
			:deep(.el-button--success:hover){
			}
			// 删除
			:deep(.el-button--danger){
			}
			// 删除-悬浮
			:deep(.el-button--danger:hover){
			}
			// 统计
			:deep(.el-button--warning){
			}
			// 统计-悬浮
			:deep(.el-button--warning:hover){
			}
		}
	}
	// 表格样式
	.el-table {
		:deep(.el-table__header-wrapper) {
			thead {
				tr {
					th {
						.cell {
						}
					}
				}
			}
		}
		:deep(.el-table__body-wrapper) {
		}
	}
	// 分页器
	.el-pagination {
		// 总页
		:deep(.el-pagination__total) {
		}
		// 上一页
		:deep(.btn-prev) {
		}
		// 下一页
		:deep(.btn-next) {
		}
		// 页码
		:deep(.el-pager) {
			// 数字
			.number {
			}
			// 数字悬浮
			.number:hover {
			}
			// 选中
			.number.is-active {
			}
		}
		// sizes
		:deep(.el-pagination__sizes) {
		}
		// 跳页
		:deep(.el-pagination__jump) {
		}
	}
</style>
