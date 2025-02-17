<template>
  <!-- show(visible)가 true일 때만 표시 -->
  <div v-if="visible" class="my-alert-backdrop">
    <div class="my-alert-dialog">
      <div class="my-alert-content">
        <p>{{ message }}</p>
      </div>
      <button @click="closeAlert" class="my-alert-btn">확인</button>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, defineProps, defineEmits } from 'vue';

const props = defineProps({
  /** 알림에 표시할 메시지 */
  message: {
    type: String,
    default: '알림 메시지',
  },
  /** 모달 표시 여부 (부모에서 v-model:show로 제어) */
  show: {
    type: Boolean,
    default: false,
  },
});

/** 부모로 상태를 알려주기 위한 emit */
const emit = defineEmits(['update:show']);

/** 내부에서 제어할 visible 상태 */
const visible = ref(props.show);

/** props.show가 변경될 때마다 visible도 동기화 */
watch(
  () => props.show,
  (newVal) => {
    visible.value = newVal;
  }
);

/** 모달 닫기 */
function closeAlert() {
  visible.value = false;
  emit('update:show', false);
}
</script>

<style scoped>
.my-alert-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  /* 배경 어둡게 처리 */
  background-color: rgba(0, 0, 0, 0.4);

  /* 중앙 정렬 */
  display: flex;
  align-items: center;
  justify-content: center;

  /* 항상 맨 위로 올리기 */
  z-index: 9999;
}

.my-alert-dialog {
  background-color: #ffffff;
  padding: 20px 16px;
  border-radius: 8px;
  min-width: 280px;

  /* 간단한 애니메이션(선택사항) */
  animation: fadeInDown 0.3s ease;
}

/* 메시지 영역 */
.my-alert-content {
  margin-bottom: 16px;
  font-size: 16px;
  color: #333;
  text-align: center;
}

/* 확인 버튼 */
.my-alert-btn {
  display: block;
  margin: 0 auto;
  background-color: #79c7e3;
  color: #ffffff;
  font-size: 14px;
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.my-alert-btn:hover {
  background-color: #68b5d3;
}

/* 페이드 인/다운 애니메이션 (선택사항) */
@keyframes fadeInDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
