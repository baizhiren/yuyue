<template>
  <el-dialog
    :title="!dataForm.aId ? '新增' : '修改'"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
    <el-form-item label="时段id" prop="tId">
      <el-input v-model="dataForm.tId" placeholder="时段id"></el-input>
    </el-form-item>
    <el-form-item label="会议室id" prop="rId">
      <el-input v-model="dataForm.rId" placeholder="会议室id"></el-input>
    </el-form-item>
    <el-form-item label="用户id" prop="uId">
      <el-input v-model="dataForm.uId" placeholder="用户id"></el-input>
    </el-form-item>
    <el-form-item label="会议室名" prop="rName">
      <el-input v-model="dataForm.rName" placeholder="会议室名"></el-input>
    </el-form-item>
    <el-form-item label="预约人姓名" prop="uName">
      <el-input v-model="dataForm.uName" placeholder="预约人姓名"></el-input>
    </el-form-item>
    <el-form-item label="开始时间" prop="startTime">
      <el-input v-model="dataForm.startTime" placeholder="开始时间"></el-input>
    </el-form-item>
    <el-form-item label="结束时间" prop="endTime">
      <el-input v-model="dataForm.endTime" placeholder="结束时间"></el-input>
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
          aId: 0,
          tId: '',
          rId: '',
          uId: '',
          rName: '',
          uName: '',
          startTime: '',
          endTime: ''
        },
        dataRule: {
          tId: [
            { required: true, message: '时段id不能为空', trigger: 'blur' }
          ],
          rId: [
            { required: true, message: '会议室id不能为空', trigger: 'blur' }
          ],
          uId: [
            { required: true, message: '用户id不能为空', trigger: 'blur' }
          ],
          rName: [
            { required: true, message: '会议室名不能为空', trigger: 'blur' }
          ],
          uName: [
            { required: true, message: '预约人姓名不能为空', trigger: 'blur' }
          ],
          startTime: [
            { required: true, message: '开始时间不能为空', trigger: 'blur' }
          ],
          endTime: [
            { required: true, message: '结束时间不能为空', trigger: 'blur' }
          ]
        }
      }
    },
    methods: {
      init (id) {
        this.dataForm.aId = id || 0
        this.visible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].resetFields()
          if (this.dataForm.aId) {
            this.$http({
              url: this.$http.adornUrl(`/backend/appointment/info/${this.dataForm.aId}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.dataForm.tId = data.appointment.tId
                this.dataForm.rId = data.appointment.rId
                this.dataForm.uId = data.appointment.uId
                this.dataForm.rName = data.appointment.rName
                this.dataForm.uName = data.appointment.uName
                this.dataForm.startTime = data.appointment.startTime
                this.dataForm.endTime = data.appointment.endTime
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
              url: this.$http.adornUrl(`/backend/appointment/${!this.dataForm.aId ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'aId': this.dataForm.aId || undefined,
                'tId': this.dataForm.tId,
                'rId': this.dataForm.rId,
                'uId': this.dataForm.uId,
                'rName': this.dataForm.rName,
                'uName': this.dataForm.uName,
                'startTime': this.dataForm.startTime,
                'endTime': this.dataForm.endTime
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
