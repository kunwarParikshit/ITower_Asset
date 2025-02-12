package com.isl.modal;

import java.util.List;

public class BeanGetImageList extends BeansErrorResponse {
	List<BeanGetImage> ImageList;

	public List<BeanGetImage> getImageList() {
		return ImageList;
	}

	public void setImageList(List<BeanGetImage> imageList) {
		ImageList = imageList;
	}

}
