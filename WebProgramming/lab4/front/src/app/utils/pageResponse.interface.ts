export interface PageDTO<T> {
  content: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
}


export interface Point {
  id: number;
  x: number;
  y: number;
  r: number;
  result: boolean;
  ownerId?: number;
}
