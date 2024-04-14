<template>
  <el-dialog
    :title="!dataForm.rId ? '新增' : '修改'"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
    <el-form-item label="会议室名" prop="name">
      <el-input v-model="dataForm.name" placeholder="会议室名"></el-input>
    </el-form-item>
    <el-form-item label="状态（0：不可用 1： 可用）" prop="status">
      <el-input v-model="dataForm.status" placeholder="状态（0：不可用 1： 可用）"></el-input>
    </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
  export default {
    data () {
      return {
        visible: false,
        dataForm: {
          rId: 0,
          name: '',
          status: ''
        },
        dataRule: {
          name: [
            { required: true, message: '会议室名不能为空', trigger: 'blur' }
          ],
          status: [
            { required: true, message: '状态（0：不可用 1： 可用）不能为空', trigger: 'blur' }
          ]
        }
      }
    },
    methods: {
      init (id) {
        this.dataForm.rId = id || 0
        this.visible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].resetFields()
          if (this.dataForm.rId) {
            this.$http({
              url: this.$http.adornUrl(`/backend/room/info/${this.dataForm.rId}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.dataForm.name = data.room.name
                this.dataForm.status = data.room.status
              }
            })
          }
        })
      },
      // 表单提交
      dataFormSubmit () {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            this.$http({
              url: this.$http.adornUrl(`/backend/room/${!this.dataForm.rId ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'rId': this.dataForm.rId || undefined,
                'name': this.dataForm.name,
                'status': this.dataForm.status
              })
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.$message({
                  message: '操作成功',
                  type: 'success',
                  duration: 1500,
                  onClose: () => {
                    this.visible = false
                    this.$emit('refreshDataList')
                  }
                })
              } else {
                this.$message.error(data.msg)
              }
            })
          }
        })
      }
    }
  }
</script>
