import { NewsItem } from "../interfaces";

type NewsItemProps = {
  item: NewsItem;
};

export default function NewsItemComponent({ item }: NewsItemProps) {
  return (
    <li>
      <h1>
        <a href={item.url}>{item.title}</a>
      </h1>
    </li>
  );
}
