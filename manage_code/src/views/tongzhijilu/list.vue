<template>
	<div>
		<div class="center_view">
			<div class="list_search_view">
				<el-form :model="searchQuery" class="search_form" >
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
							发送渠道：
						</div>
						<div class="search_box">
							<el-select class="search_sel" v-model="searchQuery.fasongqudao" placeholder="请选择发送渠道" clearable>
								<el-option label="短信" value="sms"></el-option>
								<el-option label="站内信" value="站内信"></el-option>
							</el-select>
						</div>
					</div>
					<div class="search_view">
						<div class="search_label">
							发送状态：
						</div>
						<div class="search_box">
							<el-select class="search_sel" v-model="searchQuery.fasongzhuangtai" placeholder="请选择发送状态" clearable>
								<el-option label="待发送" value="待发送"></el-option>
								<el-option label="成功" value="成功"></el-option>
								<el-option label="失败" value="失败"></el-option>
							</el-select>
						</div>
					</div>
					<div class="search_btn_view">
						<el-button class="search_btn" type="primary" @click="searchClick()" size="small">搜索</el-button>
					</div>
				</el-form>
				<div class="btn_view">
					<el-button class="del_btn" type="danger" :disabled="selRows.length?false:true" @click="delClick(null)"  v-if="btnAuth('tongzhijilu','删除')">
						<i class="iconfont icon-shanchu4"></i>
						删除
					</el-button>
					<el-button class="retry_btn" type="warning" :disabled="selRows.length?false:true" @click="retryBatchClick" v-if="btnAuth('tongzhijilu','重试')">
						<i class="iconfont icon-shuaxin"></i>
						批量重试
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
					prop="shouji"
					label="用户手机">
					<template #default="scope">
						{{scope.row.shouji}}
					</template>
				</el-table-column>
				<el-table-column min-width="140"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="yishengzhanghao"
					label="医生账号">
					<template #default="scope">
						{{scope.row.yishengzhanghao}}
					</template>
				</el-table-column>
				<el-table-column min-width="140"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="jiuzhenshijian"
					label="就诊时间">
					<template #default="scope">
						{{scope.row.jiuzhenshijian}}
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
						<el-tag v-if="scope.row.fasongqudao=='sms'" type="success">短信</el-tag>
						<el-tag v-else-if="scope.row.fasongqudao=='站内信'" type="primary">站内信</el-tag>
						<el-tag v-else>{{scope.row.fasongqudao}}</el-tag>
					</template>
				</el-table-column>
				<el-table-column min-width="100"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="fasongzhuangtai"
					label="发送状态">
					<template #default="scope">
						<el-tag v-if="scope.row.fasongzhuangtai=='成功'" type="success">成功</el-tag>
						<el-tag v-else-if="scope.row.fasongzhuangtai=='失败'" type="danger">失败</el-tag>
						<el-tag v-else type="warning">待发送</el-tag>
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
						{{scope.row.chongshicishu}}/{{scope.row.zuidachongshi}}
					</template>
				</el-table-column>
				<el-table-column min-width="140"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="fasongshijian"
					label="发送时间">
					<template #default="scope">
						{{scope.row.fasongshijian}}
					</template>
				</el-table-column>
				<el-table-column min-width="140"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="shibaoyuanyin"
					label="失败原因">
					<template #default="scope">
						{{scope.row.shibaoyuanyin || '-'}}
					</template>
				</el-table-column>
				<el-table-column label="操作" width="250" :resizable='true' :sortable='true' align="left" header-align="left">
					<template #default="scope">
						<el-button class="view_btn" type="info" v-if=" btnAuth('tongzhijilu','查看')" @click="infoClick(scope.row.id)">
							<i class="iconfont icon-sousuo2"></i>
							查看
						</el-button>
						<el-button class="retry_btn" type="warning" @click="retryClick(scope.row.id)" v-if="scope.row.fasongzhuangtai!='成功' && btnAuth('tongzhijilu','重试')">
							<i class="iconfont icon-shuaxin"></i>
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
		<formModel ref="formRef" @formModelChange="formModelChange"></formModel>
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
		ElMessageBox
	} from 'element-plus'
	import {
		useStore
	} from 'vuex';
	const store = useStore()
	const user = computed(()=>store.getters['user/session'])
	const avatar = ref(store.state.user.avatar)
	const context = getCurrentInstance()?.appContext.config.globalProperties;
	import formModel from './formModel.vue'
	const tableName = 'tongzhijilu'
	const formName = '通知发送记录'
	const route = useRoute()
	onMounted(()=>{
	})
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
	const getList = () => {
		listLoading.value = true
		let params = JSON.parse(JSON.stringify(listQuery.value))
		params['sort'] = 'id'
		params['order'] = 'desc'
		if(searchQuery.value.zhanghao&&searchQuery.value.zhanghao!=''){
			params['zhanghao'] = '%' + searchQuery.value.zhanghao + '%'
		}
		if(searchQuery.value.fasongqudao&&searchQuery.value.fasongqudao!=''){
			params['fasongqudao'] = searchQuery.value.fasongqudao
		}
		if(searchQuery.value.fasongzhuangtai&&searchQuery.value.fasongzhuangtai!=''){
			params['fasongzhuangtai'] = searchQuery.value.fasongzhuangtai
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
	const handleSelectionChange = (e) => {
		selRows.value = e
	}
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
	const btnAuth = (e,a)=>{
		return context?.$toolUtil.isAuth(e,a)
	}
	const searchClick = () => {
		listQuery.value.page = 1
		getList()
	}
	const formRef = ref(null)
	const formModelChange=()=>{
		searchClick()
	}
	const infoClick = (id=null)=>{
		if(id){
			formRef.value.init(id,'info')
		}
		else if(selRows.value.length){
			formRef.value.init(selRows.value[0].id,'info')
		}
	}
	const retryClick = (id) => {
		ElMessageBox.confirm('是否重试发送该通知?', '提示', {
			confirmButtonText: '是',
			cancelButtonText: '否',
			type: 'warning',
		}).then(() => {
			context.$http({
				url: `${tableName}/retry/${id}`,
				method: 'post'
			}).then(res => {
				if(res.data.code==0){
					context?.$toolUtil.message(res.data.msg || '已加入重试队列', 'success',()=>{
						getList()
					})
				}else{
					context?.$toolUtil.message(res.data.msg, 'error')
				}
			})
		}).catch(_ => {})
	}
	const retryBatchClick = () => {
		let ids = []
		if (selRows.value.length) {
			for (let x in selRows.value) {
				if(selRows.value[x].fasongzhuangtai != '成功'){
					ids.push(selRows.value[x].id)
				}
			}
		}
		if(ids.length == 0){
			context?.$toolUtil.message('没有可重试的记录', 'warning')
			return
		}
		ElMessageBox.confirm(`是否批量重试选中的${ids.length}条记录?`, '提示', {
			confirmButtonText: '是',
			cancelButtonText: '否',
			type: 'warning',
		}).then(() => {
			context.$http({
				url: `${tableName}/retryBatch`,
				method: 'post',
				data: ids
			}).then(res => {
				context?.$toolUtil.message('已加入重试队列', 'success',()=>{
					getList()
				})
			})
		}).catch(_ => {})
	}
	const init = () => {
		getList()
	}
	init()
</script>
<style lang="scss" scoped>
	.list_search_view {
		.search_form {
			.search_view {
				.search_label {
				}
				.search_box {
					:deep(.search_inp) {
					}
					:deep(.search_sel) {
						width: 200px;
					}
				}
			}
			.search_btn_view {
				.search_btn {
				}
				.search_btn:hover {
				}
			}
		}
		.btn_view {
			:deep(.el-button--default){
			}
			:deep(.el-button--default:hover){
			}
			:deep(.el-button--success){
			}
			:deep(.el-button--success:hover){
			}
			:deep(.el-button--danger){
			}
			:deep(.el-button--danger:hover){
			}
			:deep(.el-button--warning){
			}
			:deep(.el-button--warning:hover){
			}
		}
	}
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
			tbody {
				tr {
					td {
						.cell {
							.el-button--primary {
							}
							.el-button--primary:hover {
							}
							.el-button--info {
							}
							.el-button--info:hover {
							}
							.el-button--danger {
							}
							.el-button--danger:hover {
							}
							.el-button--warning {
							}
							.el-button--warning:hover {
							}
						}
					}
				}
				tr:hover {
					td {
					}
				}
			}
		}
	}
	.el-pagination {
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
			display: inline-block;
			vertical-align: top;
			font-size: 13px;
			line-height: 28px;
			height: 28px;
			.el-select {
			}
		}
		:deep(.el-pagination__jump) {
			.el-input {
			}
		}
	}
</style>
