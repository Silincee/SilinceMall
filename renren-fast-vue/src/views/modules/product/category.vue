<template>
<div>
<!--  拖拽控制开关-->
  <el-switch
    v-model="draggable"
    active-text="开启拖拽"
    inactive-text="关闭拖拽">
  </el-switch>

<!--  批量拖拽提交-->
  <el-button @click="batchSave" v-if="draggable">批量保存</el-button>
<!--  批量删除-->
  <el-button type="danger" @click="batchDelete">批量删除</el-button>

  <el-tree
    :data="menus"
    :props="defaultProps"
    :expand-on-click-node="false"
    show-checkbox
    node-key="catId"
    :default-expanded-keys="expandedKey"
    :draggable="draggable"
    :allow-drop="allowDrop"
    @node-drop="handleDrop"
    ref="menuTree"
  >

    <span class="custom-tree-node" slot-scope="{ node, data }">
      <span>{{ node.label }}</span>
        <span>
          <el-button v-if="node.level<=2" type="text" size="mini" @click="() => append(data)">Append</el-button>
           <el-button  type="text" size="mini" @click="() => edit(data)">Edit</el-button>
          <el-button v-if="node.childNodes.length==0" type="text" size="mini" @click="() => remove(node, data)">Delete</el-button>
        </span>
    </span>
  </el-tree>

<!--  element-ui对话框-->
  <el-dialog
    :title="title"
    :visible.sync="dialogVisible"
    width="30%"
    :close-on-click-modal="false"
  >

    <el-form :model="category">
      <el-form-item label="分类名称">
        <el-input v-model="category.name" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="图标">
        <el-input v-model="category.icon" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="计量单位">
        <el-input v-model="category.productUnit" autocomplete="off"></el-input>
      </el-form-item>
    </el-form>

    <span slot="footer" class="dialog-footer">
    <el-button @click="dialogVisible = false">取 消</el-button>
    <el-button type="primary" @click="submitData">确 定</el-button>
  </span>
  </el-dialog>

</div>


</template>

<script>
  // 这里可以导入其他文件(比如：组件，工具js，第三方插件js，json文件，图片文件等)
  // 例如 import 组件名称 from '组件路径';

  export default {
    // import引入的组件需要注入到对象中才能使用
    components: {},
    props: {},
    data () {
      return {
        pCid: [],
        draggable: false,
        updateNodes: [],
        maxLevel: 0, // 初始化节点最大层级
        title: '', // 对话框的名字
        category: {name: '', parentCid: 0, catLevel: 0, showStatus: 1, sort: 0, catId: null, icon: '', productUnit: ''},
        dialogVisible: false, // false 表示对话框关闭
        dialogType: '', // 点击edit按钮把它变为edit，点击add按钮则为add
        menus: [],
        expandedKey: [],
        defaultProps: {
          children: 'children',
          label: 'name'
        }
      }
    },

    // 计算属性 类似于data的概念
    computed: {},
    // 监控data中的数据变化
    watch: {},
    // 生命周期-创建完成(可以访问当前this实例)
    created () {
      this.getMenus()
    },
    methods: {
      getMenus () {
        this.$http({
          url: this.$http.adornUrl('/product/category/list/tree'),
          method: 'get'
        }).then(({data}) => {
          console.log('成功获取到菜单数据...', data.data)
          this.menus = data.data
        })
      },

      submitData () {
        if (this.dialogType === 'add') {
          this.addCategory()
        }
        if (this.dialogType === 'edit') {
          this.editCategory()
        }
      },
      // 添加菜单
      append (data) {
        console.log('append', data)
        this.title = '添加分类'
        this.dialogType = 'add'
        // 打开对话框
        this.dialogVisible = true
        // 自动获取自己和父节点的分类层级
        this.category.parentCid = data.catId
        this.category.catLevel = data.catLevel * 1 + 1

        // 清空之前添加或修改回写的值
        this.category.name = ''
        this.category.catId = null
        this.category.icon = ''
        this.category.productUnit = ''
      },
      // 菜单修改功能
      edit (data) {
        console.log('要修改的数据', data)
        this.title = '修改分类'
        this.dialogType = 'edit' // 修改复用对话框的类型
        this.dialogVisible = true
        this.category.parentCid = data.catId
        // 发送请求回显当前节点最新的数据
        this.$http({
          url: this.$http.adornUrl(`/product/category/info/${data.catId}`),
          method: 'get'
        }).then(({data}) => {
          // 请求成功 回显最新的数据
          console.log('要回显的数据', data)
          this.category.name = data.data.name
          this.category.catId = data.data.catId
          this.category.icon = data.data.icon
          this.category.productUnit = data.data.productUnit
          this.category.catLevel = data.catLevel
        })
      },
      remove (node, data) {
        var ids = [data.catId]
        // 删除前进行弹框提示
        this.$confirm(`是否删除【${data.name}】菜单, 是否继续?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          // 确认删除了才发送真正的请求
          this.$http({
            url: this.$http.adornUrl('/product/category/delete'),
            method: 'post',
            data: this.$http.adornData(ids, false)
          }).then(({data}) => {
            // 提示删除成功
            this.$message({
              message: '菜单删除成功',
              type: 'success'
            })
            this.getMenus() // 更新数据
            // 设置需要默认展开的菜单
            this.expandedKey = [node.parent.data.catId]
          })
        }).catch(() => {
          console.log('取消了删除')
        })
        console.log('remove', node, data)
      },
      // 修改三级分类数据
      editCategory () {
        // 只修改部分字段，为null的字段不会在数据库中修改
        // eslint-disable-next-line no-unused-vars
        var {catId, name, icon, productUnit} = this.category
        var data = {catId: catId, name: name, icon: icon, productUnit: productUnit}
        this.$http({
          url: this.$http.adornUrl('/product/category/update'),
          method: 'post',
          data: this.$http.adornData(data, false)
        }).then(({data}) => {
          this.$message({
            message: '菜单修改成功',
            type: 'success'
          })
          // 关闭对话框 然后更新数据
          this.dialogVisible = false
          this.getMenus()
          // 设置需要默认展开的菜单
          this.expandedKey = [this.category.parentCid]
        })
      },
      // 添加三级分类
      addCategory () {
        console.log('提交的三级分类数据', this.category)
        this.$http({
          url: this.$http.adornUrl('/product/category/save'),
          method: 'post',
          data: this.$http.adornData(this.category, false)
        }).then(({data}) => {
          this.$message({
            message: '菜单保存成功',
            type: 'success'
          })
          // 关闭对话框 然后更新数据
          this.dialogVisible = false
          this.getMenus()
          // 设置需要默认展开的菜单
          this.expandedKey = [this.category.parentCid]
        })
      },
      // 设置节点是否可拖拽
      allowDrop (draggingNode, dropNode, type) {
        // 1 判断被拖动的当前节点以及所在的父节点总层数不能大于3
        // 1.1 被拖动的当前节点总层数
        console.log('allowDrop:', draggingNode, dropNode, type)
        this.countNodeLevel(draggingNode)
        // 当前正在拖动的节点 + 父节点所在的深度不大于3即可
        let deep = Math.abs(this.maxLevel - draggingNode.level) + 1
        console.log('深度', deep)
        // this.maxLevel
        if (type === 'inner') {
          return (deep + dropNode.level) <= 3
        } else {
          return (deep + dropNode.parent.level) <= 3
        }
      },
      // 统计当前被拖动节点的总层数
      countNodeLevel (node) {
        // 找到所有子节点，求出最大深度
        if (node.childNodes != null && node.childNodes.length > 0) {
          for (let i = 0; i < node.childNodes.length; i++) {
            if (node.childNodes[i].level > this.maxLevel) {
              this.maxLevel = node.childNodes[i].level
            }
            this.countNodeLevel(node.childNodes[i])
          }
        }
      },
      // 拖拽完成后的调用的函数 参数列表：被拖拽节点对应的 Node、结束拖拽时最后进入的节点、被拖拽节点的放置位置（before、after、inner）
      handleDrop (draggingNode, dropNode, dropType) {
        console.log('tree drop: ', dropNode.label, dropType)
        // 1 当前节点最新的父节点id
        let pCid = 0
        let siblings = null
        if (dropType === 'before' || dropType === 'after') {
          pCid = dropNode.parent.data.catId === undefined ? 0 : dropNode.parent.data.catId
          siblings = dropNode.parent.childNodes
        } else {
          pCid = dropNode.data.catId
          siblings = dropNode.childNodes
        }
        this.pCid.push(pCid)
        // 2 当前拖住节点的最新顺序
        for (let i = 0; i < siblings.length; i++) {
          if (siblings[i].data.catId === draggingNode.data.catId) {
            // 如果遍历的是当前正在拖拽的节点
            let catLevel = draggingNode.level
            if (siblings[i].level !== draggingNode.level) {
              // 当前节点层级发生比变化
              catLevel = siblings[i].level
              // 修改它子节点的层级
              this.updateChildNodeLevel(siblings[i])
            }
            this.updateNodes.push({catId: siblings[i].data.catId, sort: i, parentCid: pCid, catLevel: catLevel})
          } else {
            this.updateNodes.push({catId: siblings[i].data.catId, sort: i})
          }
        }
        // 3 当前拖拽节点的最新层级
        console.log('updateNodes', this.updateNodes)
      },
      // 更改子节点层级
      updateChildNodeLevel (node) {
        if (node.childNodes.length > 0) {
          for (let i = 0; i < node.childNodes.length; i++) {
            var cNode = node.childNodes[i].data
            this.updateNodes.push({catId: cNode.catId, catLevel: node.childNodes[i].level})
            this.updateChildNodeLevel(node.childNodes[i])
          }
        }
      },
      // 拖拽批量保存并提交
      batchSave () {
        // 修改数据
        this.$http({
          url: this.$http.adornUrl('/product/category/update/sort'),
          method: 'post',
          data: this.$http.adornData(this.updateNodes, false)
        }).then(({data}) => {
          this.$message({
            message: '菜单顺序修改成功',
            type: 'success'
          })
        })
        // 刷新菜单，展开拖入的节点
        this.getMenus()
        this.expandedKey = this.pCid
        // 清空复位
        this.updateNodes = []
        this.maxLevel = 0
        // this.pCid = 0
      },
      // 批量删除
      batchDelete () {
        let catIds = []
        let checkedNodes = this.$refs.menuTree.getCheckedNodes()
        console.log('被选中的元素', checkedNodes)
        for (let i = 0; i < checkedNodes.length; i++) {
          catIds.push(checkedNodes[i].catId)
        }
        // 确认删除提示框
        this.$confirm(`是否批量删除【${catIds}】菜单, 是否继续?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$http({
            url: this.$http.adornUrl('/product/category/delete'),
            method: 'post',
            data: this.$http.adornData(catIds, false)
          }).then(({data}) => {
            this.$message({
              message: '菜单批量删除成功',
              type: 'success'
            })
            this.getMenus()
          })
        }).catch(() => {
          console.log('批量删除已取消')
        })
      }
    }
  }
</script>

<style>
</style>
