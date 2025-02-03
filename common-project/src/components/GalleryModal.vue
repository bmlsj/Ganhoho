<template>
  <!-- 모달이 보일 때만 렌더링 -->
  <div v-if="isVisible" class="modal-overlay" @click.self="closeModal">
    <!-- 모달 컨텐츠 -->
    <div
      ref="modal"
      class="modal-content"
      :class="{ 'full-modal': isFullScreen }"
      @touchstart="handleTouchStart"
      @touchmove="handleTouchMove"
      @touchend="handleTouchEnd"
    >
      <!-- 모달 헤더 (드래그 기능 제공) -->
      <div class="modal-header">
        <div class="drag-handle"></div>
      </div>

      <!-- 이미지 그리드 (무한 스크롤 적용) -->
      <div class="image-grid" ref="imageGrid" @scroll="handleScroll">
        <div v-for="(image, index) in displayedImages" :key="index" class="image-wrapper">
          <img :src="image" alt="Selected Image" class="grid-image"/>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from "vue";

/* 부모 컴포넌트로부터 받는 props */
const props = defineProps({
  isVisible: Boolean, // 모달이 보이는지 여부
  images: {
    type: Array,
    default: () => [], // 이미지 리스트 기본값 설정
  },
});

/* 부모 컴포넌트로 이벤트를 발생시키기 위한 emit */
const emit = defineEmits(["close"]);

/* 모달이 전체화면인지 여부 */
const isFullScreen = ref(false);
const imageGrid = ref(null);
const displayedImages = ref([]);
const itemsPerLoad = 9; // 한 번에 로드할 이미지 개수
let loadedCount = 0;
let startY = 0;
let moveY = 0;

/* ✅ 이미지 로드 */
const processImages = () => {
  displayedImages.value = props.images.map(img => img);
};

/* ✅ 뒤로 가기 시 모달만 닫기 */
const handleBackPress = () => {
  if (props.isVisible) {
    closeModal();
    history.pushState(null, document.title, location.href);
  }
};

/* ✅ 무한 스크롤 */
const handleScroll = () => {
  if (!imageGrid.value) return;
  const { scrollTop, scrollHeight, clientHeight } = imageGrid.value;
  
  if (scrollTop + clientHeight >= scrollHeight - 10) {
    loadMoreImages();
  }
};

/* ✅ 추가 이미지 로드 */
const loadMoreImages = () => {
  if (loadedCount >= props.images.length) return;
  const nextImages = props.images.slice(loadedCount, loadedCount + itemsPerLoad);
  displayedImages.value.push(...nextImages);
  loadedCount += nextImages.length;
};

/* ✅ 모달 닫기 */
const closeModal = () => {
  isFullScreen.value = false;
  emit("close");
};

/* ✅ 터치 이벤트 (모달 위로 스와이프 시 전체 화면) */
const handleTouchStart = (event) => {
  startY = event.touches[0].clientY;
};
const handleTouchMove = (event) => {
  moveY = event.touches[0].clientY - startY;
};
const handleTouchEnd = () => {
  if (moveY < -5) {
    isFullScreen.value = true; // ✅ 전체화면 활성화
  }
};

/* ✅ 모달이 열리면 실행 */
onMounted(() => {
  if (props.isVisible) {
    processImages();
    loadMoreImages();
    window.addEventListener("popstate", handleBackPress);
    history.pushState(null, document.title, location.href);
  }
});

/* ✅ 컴포넌트가 사라질 때 이벤트 제거 */
onUnmounted(() => {
  window.removeEventListener("popstate", handleBackPress);
});
</script>

<style scoped>
/* ✅ 모달 배경 */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: flex-end;
  z-index: 1000;
}

/* ✅ 모달 기본 스타일 */
.modal-content {
  background: white;
  width: 100%;
  max-width: 500px;
  border-radius: 16px 16px 0 0;
  box-shadow: 0 -4px 50px rgba(0, 0, 0, 0.15);
  padding: 16px;
  height: 50vh;
  transition: height 0.3s ease, transform 0.3s ease;
}

/* ✅ 전체 화면 모달 (커진 상태) */
.full-modal {
  height: 100vh;
}

/* ✅ 드래그 핸들 */
.drag-handle {
  width: 40px;
  height: 5px;
  background: gray;
  border-radius: 3px;
  margin: 0 auto 16px;
}

/* ✅ 이미지 그리드 */
.image-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr); /* 기본 3열 */
  gap: 10px;
  overflow-y: auto;
  max-height: 60vh;
  padding-bottom: 10px;
}

/* ✅ 전체화면일 때는 더 많은 이미지가 보이도록 */
.full-modal .image-grid {
  grid-template-columns: repeat(3, 1fr);
  max-height: 100vh;
}

/* ✅ 이미지 크기 정렬 */
.image-wrapper {
  width: 100%;
  height: auto;
  display: flex;
  align-items: center;
  justify-content: center;
  background: black; /* 배경색 (검정 또는 흰색 선택 가능) */
}

/* ✅ 일반 모달 상태의 이미지 */
.grid-image {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

/* ✅ 전체화면일 때 더 많은 이미지 표시 */
.full-modal .grid-image {
  width: 100%;
  height: auto;
  object-fit: contain; /* 이미지 비율 유지 */
}
</style>
