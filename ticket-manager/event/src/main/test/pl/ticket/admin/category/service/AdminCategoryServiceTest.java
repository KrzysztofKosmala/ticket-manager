package pl.ticket.admin.category.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.ticket.event.admin.category.model.AdminCategory;
import pl.ticket.event.admin.category.repository.AdminCategoryRepository;
import pl.ticket.event.admin.category.service.AdminCategoryService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdminCategoryServiceTest {

    @Mock
    private AdminCategoryRepository adminCategoryRepository;
    @InjectMocks
    private AdminCategoryService categoryService;


    @Test
    void shouldCreateCategory(){
        AdminCategory adminCategory = AdminCategory.builder()
                .id(1L)
                .build();

        when(adminCategoryRepository.save(any())).thenReturn(AdminCategory.builder().id(1L).build());

        AdminCategory category = categoryService.createCategory(adminCategory);

        assertThat(category).isNotNull();
        assertThat(category.getId()).isEqualTo(1L);
    }

}
