<template>
  <el-dialog
    :title="!dataForm.uId ? '新增' : '修改'"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
    <el-form-item label="微信id" prop="vId">
      <el-input v-model="dataForm.vId" placeholder="微信id"></el-input>
    </el-form-item>
    <el-form-item label="用户姓名" prop="uName">
      <el-input v-model="dataForm.uName" placeholder="用户姓名"></el-input>
    </el-form-item>
    <el-form-item label="老师姓名" prop="teacherName">
      <el-input v-model="dataForm.teacherName" placeholder="老师姓名"></el-input>
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
          uId: 0,
          vId: '',
          uName: '',
          teacherName: ''
        },
        dataRule: {
          vId: [
            { required: true, message: '微信id不能为空', trigger: 'blur' }
          ],
          uName: [
            { required: true, message: '用户姓名不能为空', trigger: 'blur' }
          ],
          teacherName: [
            { required: true, message: '老师姓名不能为空', trigger: 'blur' }
          ]
        }
      }
    },
    methods: {
      init (id) {
        this.dataForm.uId = id || 0
        this.visible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].resetFields()
          if (this.dataForm.uId) {
            this.$http({
              url: this.$http.adornUrl(`/backend/user/info/${this.dataForm.uId}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.dataForm.vId = data.user.vId
                this.dataForm.uName = data.user.uName
                this.dataForm.teacherName = data.user.teacherName
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
              url: this.$http.adornUrl(`/backend/user/${!this.dataForm.uId ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'uId': this.dataForm.uId || undefined,
                'vId': this.dataForm.vId,
                'uName': this.dataForm.uName,
                'teacherName': this.dataForm.teacherName
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
