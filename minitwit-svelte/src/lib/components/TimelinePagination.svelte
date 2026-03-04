<script lang="ts">
    import { goto } from '$app/navigation';
    import * as Pagination from '$lib/components/ui/pagination/index.js';
    
    let { total, page }: { total: number; page: number } = $props();
</script>

<Pagination.Root 
    count={total}
    perPage={30} 
    page={page + 1}
    onPageChange={(p) => goto(`?page=${p - 1}`)}
>
    {#snippet children({ pages, currentPage })}
        <Pagination.Content>
            <Pagination.Item>
                <Pagination.Previous />
            </Pagination.Item>
            {#each pages as page (page.key)}
                {#if page.type === "ellipsis"}
                    <Pagination.Item>
                        <Pagination.Ellipsis />
                    </Pagination.Item>
                {:else}
                <Pagination.Item>
                    <Pagination.Link 
                        {page}
                        isActive={currentPage === page.value}
                        href="?page={page.value - 1}"
                        class={currentPage === page.value ? "text-[#1d9bf0]!" : ""} 
                        >                            
                            {page.value}
                    </Pagination.Link>
                </Pagination.Item>
                {/if}
            {/each}
            <Pagination.Item>
                <Pagination.Next />
            </Pagination.Item>
        </Pagination.Content>
    {/snippet}
</Pagination.Root>